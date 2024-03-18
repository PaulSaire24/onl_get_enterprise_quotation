package com.bbva.rbvd.lib.r407.impl;


import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.pisd.dto.insurancedao.entities.QuotationModEntity;
import com.bbva.pisd.dto.insurancedao.join.QuotationJoinQuotationModDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.IdentityDocumentDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ContactDetailsDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ContactDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PlanDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EmployeesDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.CoverageDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.InstallmentPlansDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ValidityPeriodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.RelatedContractsDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.AssistanceBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.CoverageBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.PlanBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.FinancingBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.InstallmentFinancingBO;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.InputQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.RBVDErrors;
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


	/**
	 * The execute method...
	 */
	@Override
	public EnterpriseQuotationDTO executeGetQuotationLogic(String quotationId,String traceId) {

		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | START");

		EnterpriseQuotationDTO response = new EnterpriseQuotationDTO();

		Map<String, Object> responseProductMap = getProductInformation(quotationId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseProductMap: {}",responseProductMap);

		if(ValidateUtils.mapIsNullOrEmpty(responseProductMap)){
			this.addAdviceWithDescription(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getAdviceCode(),
					RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getMessage());
			return null;
		}

		QuotationJoinQuotationModDTO responseQuotation = this.pisdR601.executeFindQuotationInfoByQuotationId(quotationId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseQuotation: {}",responseQuotation);

		if(responseQuotation == null){
			this.addAdviceWithDescription(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getAdviceCode(),
					RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getMessage());
			return null;
		}

		Map<String, Object> employeeInfoMap = getEmployeesInfoFromDB(quotationId, responseProductMap, responseQuotation);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | employeeInfo: {}",employeeInfoMap);

		Map<String,Object> paymentDetailsMap = getPaymentDetailsByQuotationFromDB(quotationId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | paymentDetailsMap: {}",paymentDetailsMap);

		String quotationReference = responseQuotation.getQuotation().getRfqInternalId();

		ResponseQuotationDetailBO responseRimac = callRimacService(responseProductMap,quotationReference,traceId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseRimac: {}",responseRimac);

		if(responseRimac == null){
			this.addAdviceWithDescription(RBVDErrors.ERROR_CALL_QUOTATION_DETAIL_API.getAdviceCode(),
					RBVDErrors.ERROR_CALL_QUOTATION_DETAIL_API.getMessage());
			return null;
		}

		response.setId(quotationId);
		response.setQuotationDate(ConvertUtils.convertStringDateToLocalDate(responseQuotation.getQuotation().getQuoteDate()));
		response.setEmployees(createEmployeesDTO(employeeInfoMap));
		response.setProduct(createProductDTO(responseRimac.getPayload(),responseQuotation));
		response.setContactDetails(createContactDetailsDTO(responseQuotation.getQuotationMod()));
		response.setValidityPeriod(createValidityPeriodDTO(responseRimac.getPayload().getPlan()));
		response.setBusinessAgent(createBusinessAgentDTO(responseQuotation.getQuotation().getUserAuditId()));
		response.setParticipants(createParticipantsDTO(responseQuotation.getQuotation()));
		response.setQuotationReference(quotationReference);
		response.setStatus(null);

		if(ValidateUtils.mapIsNullOrEmpty(paymentDetailsMap)){
			response.setPaymentMethod(null);
			response.setBank(null);
		}else{
			response.setPaymentMethod(createPaymentMethodDTO(paymentDetailsMap));
			response.setBank(createBankDTO(paymentDetailsMap));
		}

		return response;
	}

	private PaymentMethodDTO createPaymentMethodDTO(Map<String,Object> paymentDetailsMap){
		String paymentType = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE);
		String frequency = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_FREQUENCY_NAME);

		if(ValidateUtils.allValuesNotNullOrEmpty(paymentType,frequency)){
			PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();

			paymentMethodDTO.setPaymentType(ConstantsUtil.StringConstants.S.equalsIgnoreCase(paymentType)
					? ConstantsUtil.PaymentMethod.METHOD_TYPE_DIRECT_DEBIT
					: ConstantsUtil.PaymentMethod.METHOD_TYPE_CREDIT_CARD);
			paymentMethodDTO.setInstallmentFrequency(this.applicationConfigurationService.getProperty(
					ConvertUtils.convertStringToUpperAndLowerCase(frequency)));
			paymentMethodDTO.setRelatedContracts(constructRelatedContracts(paymentDetailsMap));

			return paymentMethodDTO;
		}
		return null;
	}

	private List<RelatedContractsDTO> constructRelatedContracts(Map<String,Object> paymentDetailsMap){
		String contractId = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_DOMICILE_CONTRACT_ID);
		String paymentMethodType = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_METHOD_TYPE);

		if(ValidateUtils.allValuesNotNullOrEmpty(contractId,paymentMethodType)){
			List<RelatedContractsDTO> relatedContractList = new ArrayList<>();
			RelatedContractsDTO relatedContractsDTO = new RelatedContractsDTO();

			relatedContractsDTO.setContractId(contractId);
			relatedContractsDTO.setNumber(contractId);
			DescriptionDTO productDTO = new DescriptionDTO();
			productDTO.setId("T".equalsIgnoreCase(paymentMethodType)
					? ConstantsUtil.PaymentMethod.PRODUCT_ID_CARD : ConstantsUtil.PaymentMethod.PRODUCT_ID_ACCOUNT);
			relatedContractsDTO.setProduct(productDTO);

			relatedContractList.add(relatedContractsDTO);

			return relatedContractList;
		}

		return null;
	}


	private BankDTO createBankDTO(Map<String,Object> paymentDetailsMap){
		String entity = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_INSURANCE_CONTRACT_ENTITY_ID);
		String branch = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_CONTRACT_MANAGER_BRANCH_ID);

		if(ValidateUtils.allValuesNotNullOrEmpty(entity,branch)){
			BankDTO bankDTO = new BankDTO();
			bankDTO.setId(entity);

			DescriptionDTO branchDTO = new DescriptionDTO();
			branchDTO.setId(branch);
			bankDTO.setBranch(branchDTO);

			return bankDTO;
		}

		return null;
	}

	private ResponseQuotationDetailBO callRimacService(Map<String,Object> responseProductMap,String quotationReference,
													   String traceId){
			String externalQuotationId = (String) responseProductMap.get(ConstantsUtil.QuotationMap.INSURANCE_COMPANY_QUOTA_ID);
		String productShortDesc = (String) responseProductMap.get(ConstantsUtil.InsuranceProduct.FIELD_PRODUCT_SHORT_DESC);

		String quotationType;

		if(ValidateUtils.stringIsNullOrEmpty(quotationReference)){
			quotationType = ConstantsUtil.StringConstants.R;
		}else{
			quotationType = ConstantsUtil.StringConstants.C;
		}

		InputQuotationDetailBO inputRimac = new InputQuotationDetailBO();
		inputRimac.setCotizacion(externalQuotationId);
		inputRimac.setProducto(productShortDesc);
		inputRimac.setTipoCotizacion(quotationType);
		inputRimac.setTraceId(traceId);

		return executeQuotationDetailRimac(inputRimac);
	}

	private Map<String,Object> getPaymentDetailsByQuotationFromDB(String quotationId){
		Map<String,Object> arguments = new HashMap<>();
		arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
		return this.pisdR402.executeGetASingleRow(
				ConstantsUtil.QueriesName.QUERY_FIND_PAYMENTMETHOD_FROM_QUOTATION,arguments);
	}


	private Map<String, Object> getEmployeesInfoFromDB(String quotationId, Map<String, Object> responseProductMap,
													   QuotationJoinQuotationModDTO responseQuotation) {

		BigDecimal insuranceProductId = ConvertUtils.getBigDecimalValue(responseProductMap.get(
				ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_PRODUCT_ID));
		Map<String,Object> arguments = new HashMap<>();
		arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
		arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_PRODUCT_ID,insuranceProductId);
		arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE,
				responseQuotation.getQuotationMod().getInsuranceModalityType());
		return this.pisdR402.executeGetASingleRow(
				ConstantsUtil.QueriesName.QUERY_FIND_ENTERPRISE_EMPLOYEE_FROM_QUOTATION,arguments);
	}

	private EmployeesDTO createEmployeesDTO(Map<String,Object> employeeInfo){
		if(!ValidateUtils.mapIsNullOrEmpty(employeeInfo) && ValidateUtils.mapNotContainsNullValue(employeeInfo)){
			EmployeesDTO employees = new EmployeesDTO();

			String areMajority = (String) employeeInfo.get(ConstantsUtil.InsuranceQuoteCoLife.FIELD_AGE_EMPLOYEES_IND_TYPE);
			employees.setAreMajorityAge(areMajority.equals("1"));

			BigDecimal employeesNumber = ConvertUtils.getBigDecimalValue(employeeInfo.get(
					ConstantsUtil.InsuranceQuoteCoLife.FIELD_PAYROLL_EMPLOYEE_NUMBER));
			employees.setEmployeesNumber(employeesNumber.longValue());

			AmountDTO payrollAmount = new AmountDTO();
			BigDecimal amount = ConvertUtils.getBigDecimalValue(employeeInfo.get(
					ConstantsUtil.InsuranceQuoteCoLife.FIELD_INCOMES_PAYROLL_AMOUNT));
			payrollAmount.setAmount(amount.doubleValue());
			payrollAmount.setCurrency((String) employeeInfo.get(ConstantsUtil.InsuranceQuoteCoLife.FIELD_CURRENCY_ID));
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

			if(ValidateUtils.allValuesNotNullOrEmpty(
					quotationEntity.getPersonalDocType(),quotationEntity.getParticipantPersonalId())){
				participantHolder.setIdentityDocument(getIdentityDocumentFromDB(quotationEntity));
			}

			DescriptionDTO participantType = new DescriptionDTO();
			participantType.setId(ConstantsUtil.StringConstants.PARTICIPANT_TYPE_HOLDER);
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
					ConstantsUtil.ContactDetailtype.EMAIL,quotationModEntity.getContactEmailDesc());
			contacts.add(contactDetailsDTO);
		}

		if(!ValidateUtils.stringIsNullOrEmpty(quotationModEntity.getCustomerPhoneDesc())){
			ContactDetailsDTO contactDetailsDTO = createContactDetailPerType(
					ConstantsUtil.ContactDetailtype.MOBILE,quotationModEntity.getCustomerPhoneDesc());
			contacts.add(contactDetailsDTO);
		}

		return contacts;
	}

	private ContactDetailsDTO createContactDetailPerType(String contactDetailType,String contactData){
		ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();
		ContactDTO contactDTO = new ContactDTO();

		if(ConstantsUtil.ContactDetailtype.EMAIL.equalsIgnoreCase(contactDetailType)){
			contactDTO.setContactDetailType(ConstantsUtil.ContactDetailtype.EMAIL);
			contactDTO.setAddress(contactData);
		}else if(ConstantsUtil.ContactDetailtype.MOBILE.equalsIgnoreCase(contactDetailType)){
			contactDTO.setContactDetailType(ConstantsUtil.ContactDetailtype.MOBILE);
			contactDTO.setNumber(contactData);
		}

		contactDetailsDTO.setContact(contactDTO);

		return contactDetailsDTO;
	}

	private ProductDTO createProductDTO(ResponsePayloadQuotationDetailBO payload,
										QuotationJoinQuotationModDTO responseQuotation){
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
		String coverageId = this.applicationConfigurationService.getProperty(
				ConstantsUtil.StringConstants.COVERAGE_TYPE_PREFIX + cobertura.getCondicion());
		String coverageName = this.applicationConfigurationService.getProperty(
				cobertura.getCondicion() + ConstantsUtil.StringConstants.COVERAGE_NAME_SUFFIX);
		coverageType.setId(coverageId);
		coverageType.setName(coverageName);
		return coverageType;
	}

	private List<InstallmentPlansDTO> createInstallmentPlansDTO(PlanBO planBO){
		List<InstallmentPlansDTO> installmentPlansDTOS = new ArrayList<>();

		FinancingBO financingBO = planBO.getFinanciamientos().stream().filter(
				financing -> ConstantsUtil.FinancingPeriodicity.ANUAL.equalsIgnoreCase(financing.getPeriodicidad()))
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

			if(ValidateUtils.allValuesNotNullOrEmpty(fechaFin,fechaInicio)){
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
		arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
		return (Map<String,Object>) this.pisdR401.executeGetProductById(
				ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC,arguments);
	}

	private ResponseQuotationDetailBO executeQuotationDetailRimac(InputQuotationDetailBO params){

		LOGGER.info("RBVDR407Impl - executeQuotationDetailRimac() | input params: {}",
				ConvertUtils.getRequestJsonFormat(params));

		String externalQuotationId = params.getCotizacion();
		String productName = params.getProducto();
		String quotationType = params.getTipoCotizacion();
		ResponseEntity<ResponseQuotationDetailBO> rimacResponse = null;
		ResponseQuotationDetailBO rimacResponseBody = null;

		String uri = this.applicationConfigurationService.getProperty(ConstantsUtil.QuotationDetailRimac.KEY_URI_FROM_CONSOLE);
		uri = uri.replace(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_EXTERNAL_QUOTATION_ID,externalQuotationId)
				.replace(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_PRODUCT_NAME,productName);
		String queryString = ConstantsUtil.QuotationDetailRimac.QUERY_STRING_TIPO_COTIZACION + quotationType;

		SignatureAWS signatureAws = this.pisdR014.executeSignatureConstruction(null, javax.ws.rs.HttpMethod.GET, uri,
				queryString, params.getTraceId());
		HttpEntity<String> entity = new HttpEntity<>(createHttpHeadersAWS(signatureAws));

		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_EXTERNAL_QUOTATION_ID, externalQuotationId);
		pathParams.put(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_PRODUCT_NAME, productName);
		pathParams.put(ConstantsUtil.QuotationDetailRimac.QUERY_PARAM_QUOTATION_TYPE,quotationType);

		try {
			rimacResponse = this.externalApiConnector.exchange(ConstantsUtil.QuotationDetailRimac.KEY_RIMAC_SERVICE,
					HttpMethod.GET, entity,ResponseQuotationDetailBO.class,pathParams);
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
		headers.set(ConstantsUtil.HeaderSignatureAWS.AUTHORIZATION, signature.getAuthorization());
		headers.set(ConstantsUtil.HeaderSignatureAWS.X_AMZ_DATE, signature.getxAmzDate());
		headers.set(ConstantsUtil.HeaderSignatureAWS.X_API_KEY, signature.getxApiKey());
		headers.set(ConstantsUtil.HeaderSignatureAWS.TRACEID, signature.getTraceId());
		return headers;
	}

}
