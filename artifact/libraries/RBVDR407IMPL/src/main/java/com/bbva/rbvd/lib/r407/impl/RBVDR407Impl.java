package com.bbva.rbvd.lib.r407.impl;


import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.pisd.dto.insurancedao.entities.QuotationModEntity;
import com.bbva.pisd.dto.insurancedao.join.QuotationJoinQuotationModDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.*;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.AssistanceBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.CoverageBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.PlanBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.FinancingBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.InstallmentFinancingBO;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.InputQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;


import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
 * The RBVDR407Impl class...
 */
public class RBVDR407Impl extends RBVDR407Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR407Impl.class);

	private static final String CONTACT_EMAIL = "EMAIL";
	private static final String CONTACT_PHONE = "MOBILE";

	/**
	 * The execute method...
	 */
	@Override
	public EnterpriseQuotationDTO executeGetQuotationLogic(String quotationId,String traceId) {

		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | START");

		EnterpriseQuotationDTO response = new EnterpriseQuotationDTO();

		Map<String, Object> responseProductMap = getProductInformation(quotationId);

		if(ValidateUtils.mapIsNullOrEmpty(responseProductMap)){
			this.addAdviceWithDescription("RBVD00000129","La cotización no existe");
			return null;
		}

		QuotationJoinQuotationModDTO responseQuotation = this.pisdR601.executeFindQuotationInfoByQuotationId(quotationId);

		if(responseQuotation == null){
			this.addAdviceWithDescription("RBVD00000129","La cotización no existe");
			return null;
		}

		Map<String, Object> employeeInfo = getEmployeesInfoFromDB(quotationId, responseProductMap, responseQuotation);

		String quotationReference = responseQuotation.getQuotation().getRfqInternalId();
		String quotationType;

		if(ValidateUtils.stringIsNullOrEmpty(quotationReference)){
			quotationType = "R";
		}else{
			quotationType = "C";
		}

		String externalQuotationId = (String) responseProductMap.get("INSURANCE_COMPANY_QUOTA_ID");
		String productShortDesc = (String) responseProductMap.get("PRODUCT_SHORT_DESC");

		InputQuotationDetailBO inputRimac = new InputQuotationDetailBO();
		inputRimac.setCotizacion(externalQuotationId);
		inputRimac.setProducto(productShortDesc);
		inputRimac.setTipoCotizacion(quotationType);
		inputRimac.setTraceId(traceId);

		ResponseQuotationDetailBO responseRimac = executeQuotationDetailRimac(inputRimac);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseRimac: {}",responseRimac);

		if(responseRimac == null){
			this.addAdviceWithDescription("RBVD00000174",
					"Error al llamar al servicio detalle cotizacion de Rimac");
			return null;
		}

		response.setId(quotationId);
		response.setQuotationDate(ConvertUtils.convertStringDateToLocalDate(responseQuotation.getQuotation().getQuoteDate()));
		response.setEmployees(createEmployeesDTO(employeeInfo));
		response.setProduct(createProductDTO(responseRimac.getPayload(),responseQuotation));
		response.setContactDetails(createContactDetailsDTO(responseQuotation.getQuotationMod()));
		response.setValidityPeriod(createValidityPeriodDTO(responseRimac.getPayload().getPlan()));
		response.setBusinessAgent(createBusinessAgentDTO(responseQuotation.getQuotation().getUserAuditId()));
		response.setParticipants(createParticipantsDTO(responseQuotation.getQuotation()));
		response.setQuotationReference(quotationReference);
		response.setStatus(null);

		return response;
	}


	private Map<String, Object> getEmployeesInfoFromDB(String quotationId, Map<String, Object> responseProductMap,
													   QuotationJoinQuotationModDTO responseQuotation) {

		BigDecimal insuranceProductId = new BigDecimal((Integer) responseProductMap.get("INSURANCE_PRODUCT_ID"));
		Map<String,Object> arguments = new HashMap<>();
		arguments.put("POLICY_QUOTA_INTERNAL_ID", quotationId);
		arguments.put("INSURANCE_PRODUCT_ID",insuranceProductId);
		arguments.put("INSURANCE_MODALITY_TYPE", responseQuotation.getQuotationMod().getInsuranceModalityType());
		return this.pisdR402.executeGetASingleRow("PISD.FIND_ENTERPRISE_EMPLOYEE_FROM_QUOTATION",arguments);
	}

	private EmployeesDTO createEmployeesDTO(Map<String,Object> employeeInfo){
		if(!ValidateUtils.mapIsNullOrEmpty(employeeInfo) && ValidateUtils.mapNotContainsNullValue(employeeInfo)){
			EmployeesDTO employees = new EmployeesDTO();

			String areMajority = (String) employeeInfo.get("YEARS_OLD_18_65_EMPLOYEES_IND_TYPE");
			employees.setAreMajorityAge(areMajority.equals("1"));

			BigDecimal employeesNumber = ConvertUtils.getBigDecimalValue(employeeInfo.get("PAYROLL_EMPLOYEE_NUMBER"));
			employees.setEmployeesNumber(employeesNumber.longValue());

			AmountDTO payrollAmount = new AmountDTO();
			BigDecimal amount = ConvertUtils.getBigDecimalValue(employeeInfo.get("INCOMES_PAYROLL_AMOUNT"));
			payrollAmount.setAmount(amount.doubleValue());
			payrollAmount.setCurrency((String) employeeInfo.get("CURRENCY_ID"));
			employees.setMonthlyPayrollAmount(payrollAmount);

			return employees;
		}
		return null;
	}

	private List<ParticipantDTO> createParticipantsDTO(QuotationEntity quotationEntity){
		List<ParticipantDTO> participantDTOS = new ArrayList<>();

		if(!ValidateUtils.stringIsNullOrEmpty(quotationEntity.getCustomerId())){
			ParticipantDTO participantHolder = new ParticipantDTO();

			participantHolder.setId(quotationEntity.getCustomerId());

			if(!ValidateUtils.stringIsNullOrEmpty(quotationEntity.getPersonalDocType()) &&
					!ValidateUtils.stringIsNullOrEmpty(quotationEntity.getParticipantPersonalId())){
				participantHolder.setIdentityDocument(getIdentityDocumentFromDB(quotationEntity));
			}

			DescriptionDTO participantType = new DescriptionDTO();
			participantType.setId("HOLDER");
			participantHolder.setParticipantType(participantType);

			participantDTOS.add(participantHolder);
		}

		return participantDTOS;
	}

	private IdentityDocumentDTO getIdentityDocumentFromDB(QuotationEntity quotationEntity) {
		IdentityDocumentDTO identityDocument = new IdentityDocumentDTO();

		DescriptionDTO documentType = new DescriptionDTO();
		documentType.setId(this.applicationConfigurationService.getProperty(quotationEntity.getPersonalDocType()));
		identityDocument.setDocumentNumber(quotationEntity.getParticipantPersonalId());
		identityDocument.setDocumentType(documentType);

		return identityDocument;
	}

	private DescriptionDTO createBusinessAgentDTO(String auditId){
		if(ValidateUtils.stringIsNullOrEmpty(auditId)){
			return null;
		}else{
			DescriptionDTO businessAgent = new DescriptionDTO();
			businessAgent.setId(auditId);

			return businessAgent;
		}
	}

	private List<ContactDetailsDTO> createContactDetailsDTO(QuotationModEntity quotationModEntity){
		List<ContactDetailsDTO> contacts = new ArrayList<>();

		if(!ValidateUtils.stringIsNullOrEmpty(quotationModEntity.getContactEmailDesc())){
			ContactDetailsDTO contactDetailsDTO = createContactDetailPerType(
					CONTACT_EMAIL,quotationModEntity.getContactEmailDesc());
			contacts.add(contactDetailsDTO);
		}

		if(!ValidateUtils.stringIsNullOrEmpty(quotationModEntity.getCustomerPhoneDesc())){
			ContactDetailsDTO contactDetailsDTO = createContactDetailPerType(
					CONTACT_PHONE,quotationModEntity.getCustomerPhoneDesc());
			contacts.add(contactDetailsDTO);
		}

		return contacts;
	}

	private ContactDetailsDTO createContactDetailPerType(String contactDetailType,String contactData){
		ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();
		ContactDTO contactDTO = new ContactDTO();

		if(CONTACT_EMAIL.equalsIgnoreCase(contactDetailType)){
			contactDTO.setContactDetailType(CONTACT_EMAIL);
			contactDTO.setAddress(contactData);
		}else if(CONTACT_PHONE.equalsIgnoreCase(contactDetailType)){
			contactDTO.setContactDetailType(CONTACT_PHONE);
			contactDTO.setNumber(contactData);
		}

		contactDetailsDTO.setContact(contactDTO);

		return contactDetailsDTO;
	}

	private ProductDTO createProductDTO(ResponsePayloadQuotationDetailBO payload,QuotationJoinQuotationModDTO responseQuotation){
		ProductDTO productDTO = new ProductDTO();

		productDTO.setId(responseQuotation.getInsuranceProductType());
		productDTO.setName(payload.getProducto());
		productDTO.setPlans(createPlansDTO(payload.getPlan(),responseQuotation.getQuotationMod().getInsuranceModalityType()));

		return productDTO;
	}

	private List<PlanDTO> createPlansDTO(PlanBO planBO,String plan){
		if(planBO != null){
			List<PlanDTO> plans = new ArrayList<>();
			PlanDTO planDTO = new PlanDTO();

			planDTO.setId(plan);
			planDTO.setName(planBO.getDescripcionPlan());
			planDTO.setIsSelected(Boolean.TRUE);
			planDTO.setTotalInstallment(createTotalInstallmentDTO(planBO));
			planDTO.setInstallmentPlans(createInstallmentPlansDTO(planBO));
			planDTO.setCoverages(createCoveragesDTO(planBO.getCoberturas()));
			planDTO.setBenefits(createBenefitsDTO(planBO.getAsistencias()));

			plans.add(planDTO);

			return plans;
		}else{
			return Collections.emptyList();
		}
	}

	private List<DescriptionDTO> createBenefitsDTO(List<AssistanceBO> asistencias){
		if(CollectionUtils.isEmpty(asistencias)){
			return Collections.emptyList();
		}else{
			return asistencias.stream().map(this::getBenefitDTOFromRimac).collect(Collectors.toList());
		}
	}

	private DescriptionDTO getBenefitDTOFromRimac(AssistanceBO asistencia) {
		DescriptionDTO benefit = new CoverageDTO();
		benefit.setId(asistencia.getAsistencia().toString());
		benefit.setName(asistencia.getDescripcionAsistencia());

		return benefit;
	}

	private List<CoverageDTO> createCoveragesDTO(List<CoverageBO> coberturas){
		if(CollectionUtils.isEmpty(coberturas)){
			return Collections.emptyList();
		}else{
			return coberturas.stream().map(this::getCoverageDTOFromRimac).collect(Collectors.toList());
		}
	}

	private CoverageDTO getCoverageDTOFromRimac(CoverageBO cobertura) {
		CoverageDTO coverageDTO = new CoverageDTO();
		coverageDTO.setId(cobertura.getCobertura().toString());
		coverageDTO.setName(cobertura.getDescripcionCobertura());
		coverageDTO.setDescription(cobertura.getObservacionCobertura());
		coverageDTO.setCoverageType(getCoverageTypeDTO(cobertura));

		return coverageDTO;
	}

	private DescriptionDTO getCoverageTypeDTO(CoverageBO cobertura) {
		DescriptionDTO coverageType = new DescriptionDTO();
		String coverageId = this.applicationConfigurationService.getProperty("COVERAGE_TYPE_" + cobertura.getCondicion());
		String coverageName = this.applicationConfigurationService.getProperty(cobertura.getCondicion() + "_COVERAGE_NAME");
		coverageType.setId(coverageId);
		coverageType.setName(coverageName);
		return coverageType;
	}

	private List<InstallmentPlansDTO> createInstallmentPlansDTO(PlanBO planBO){
		List<InstallmentPlansDTO> installmentPlansDTOS = new ArrayList<>();

		FinancingBO financingBO = planBO.getFinanciamientos().stream().filter(
				financing -> "Anual".equalsIgnoreCase(financing.getPeriodicidad()))
				.findFirst().orElse(null);

		if(financingBO != null){
			InstallmentPlansDTO installmentPlan = new InstallmentPlansDTO();

			installmentPlan.setPaymentsTotalNumber(financingBO.getNumeroCuotas());
			installmentPlan.setPaymentAmount(createPaymentAmountDTO(
					financingBO.getCuotasFinanciamiento(),planBO.getMoneda()));
			installmentPlan.setPeriod(createPeriodDTO(financingBO.getPeriodicidad()));

			installmentPlansDTOS.add(installmentPlan);
		}

		return installmentPlansDTOS;

	}

	private DescriptionDTO createPeriodDTO(String periodicity){
		DescriptionDTO period = new DescriptionDTO();
		period.setId(this.applicationConfigurationService.getProperty(periodicity));
		period.setName(periodicity.toUpperCase());

		return period;
	}

	private AmountDTO createPaymentAmountDTO(List<InstallmentFinancingBO> cuotasFinanciamiento,String moneda){
		if(CollectionUtils.isEmpty(cuotasFinanciamiento)){
			return null;
		}else{
			AmountDTO paymentAmount = new AmountDTO();
			paymentAmount.setAmount(cuotasFinanciamiento.get(0).getMonto().doubleValue());
			paymentAmount.setCurrency(moneda);

			return paymentAmount;
		}
	}

	private AmountDTO createTotalInstallmentDTO(PlanBO planBO){
		AmountDTO totalInstallment = new AmountDTO();
		totalInstallment.setAmount(planBO.getPrimaBruta().doubleValue());
		totalInstallment.setCurrency(planBO.getMoneda());

		return totalInstallment;
	}

	private ValidityPeriodDTO createValidityPeriodDTO(PlanBO planRimac){
		if(planRimac != null){
			String fechaInicio = planRimac.getFechaInicio();
			String fechaFin = planRimac.getFechaFin();

			if(!ValidateUtils.stringIsNullOrEmpty(fechaInicio) && !ValidateUtils.stringIsNullOrEmpty(fechaFin)){
				ValidityPeriodDTO validityPeriod = new ValidityPeriodDTO();
				validityPeriod.setStartDate(ConvertUtils.convertStringDateToDate(fechaInicio));
				validityPeriod.setEndDate(ConvertUtils.convertStringDateToDate(fechaFin));

				return validityPeriod;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	private Map<String, Object> getProductInformation(String quotationId) {
		Map<String,Object> arguments = new HashMap<>();
		arguments.put("POLICY_QUOTA_INTERNAL_ID", quotationId);
		return (Map<String,Object>) this.pisdR401.executeGetProductById(
				"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",arguments);
	}

	private ResponseQuotationDetailBO executeQuotationDetailRimac(InputQuotationDetailBO params){

		LOGGER.info("RBVDR407Impl - executeQuotationDetailRimac() | input params: {}", ConvertUtils.getRequestJsonFormat(params));

		String externalQuotationId = params.getCotizacion();
		String productName = params.getProducto();
		String quotationType = params.getTipoCotizacion();
		ResponseEntity<ResponseQuotationDetailBO> rimacResponse = null;
		ResponseQuotationDetailBO rimacResponseBody = null;

		String uri = this.applicationConfigurationService.getProperty("rimac.quotationdetail.enterprise.uri");
		uri = uri.replace("externalQuotationId",externalQuotationId).replace("productName",productName);
		String queryString = "tipoCotizacion=" + quotationType;

		SignatureAWS signatureAws = this.pisdR014.executeSignatureConstruction(null, javax.ws.rs.HttpMethod.GET, uri,
				queryString, params.getTraceId());
		HttpEntity<String> entity = new HttpEntity<>(createHttpHeadersAWS(signatureAws));

		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("externalQuotationId", externalQuotationId);
		pathParams.put("productName", productName);
		pathParams.put("quotationType",quotationType);

		try {
			rimacResponse = this.externalApiConnector.exchange("quotationdetail.enterprise.life",HttpMethod.GET,
					entity,ResponseQuotationDetailBO.class,pathParams);
			rimacResponseBody = rimacResponse.getBody();

			LOGGER.info("RBVDR407Impl - executeQuotationDetailRimac() | response rimac body: {}",
					ConvertUtils.getRequestJsonFormat(rimacResponseBody));
			return rimacResponseBody;
		}catch (RestClientException ex){
			LOGGER.error("RBVDR407Impl - executeQuotationDetailRimac() | RestClientException message {}",ex.getMessage());
			return null;
		}

	}

	private HttpHeaders createHttpHeadersAWS(final SignatureAWS signature) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set("Authorization", signature.getAuthorization());
		headers.set("X-Amz-Date", signature.getxAmzDate());
		headers.set("x-api-key", signature.getxApiKey());
		headers.set("traceId", signature.getTraceId());
		return headers;
	}

}
