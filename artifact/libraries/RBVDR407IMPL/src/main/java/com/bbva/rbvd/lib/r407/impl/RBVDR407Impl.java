package com.bbva.rbvd.lib.r407.impl;


import com.bbva.pisd.dto.insurancedao.join.QuotationJoinQuotationModDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ValidityPeriodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.PlanBO;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.InputQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.RBVDErrors;
import com.bbva.rbvd.lib.r407.impl.business.IEmployeesBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IPaymentBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IProductBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IParticipantsBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IContactDetailsBusiness;
import com.bbva.rbvd.lib.r407.impl.business.impl.ContactDetailsBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.EmployeesBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.PariticipantsBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.PaymentBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.ProductBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.service.api.ConsumerExternalService;
import com.bbva.rbvd.lib.r407.impl.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r407.impl.service.dao.IQuotationDAO;
import com.bbva.rbvd.lib.r407.impl.service.dao.impl.ProductDAOImpl;
import com.bbva.rbvd.lib.r407.impl.service.dao.impl.QuotationDAOImpl;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.util.Map;


/**
 * The RBVDR407Impl class...
 */
public class RBVDR407Impl extends RBVDR407Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR407Impl.class);


	/**
	 * The executeGetQuotationLogic method...
	 */
	@Override
	public EnterpriseQuotationDTO executeGetQuotationLogic(String quotationId,String traceId) {

		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | START");

		EnterpriseQuotationDTO response = new EnterpriseQuotationDTO();

		IProductDAO productDAO = new ProductDAOImpl(this.pisdR401);
		Map<String, Object> responseProductMap = productDAO.getProductInformation(quotationId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseProductMap: {}",responseProductMap);

		if(ValidateUtils.mapIsNullOrEmpty(responseProductMap)){
			this.addAdviceWithDescription(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getAdviceCode(),
					RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getMessage());
			return null;
		}

		QuotationJoinQuotationModDTO responseQuotation = this.pisdR601.executeFindQuotationInfoByQuotationId(quotationId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseQuotation: {}",responseQuotation);

		if(responseQuotation == null || responseQuotation.getQuotation() == null ||
				responseQuotation.getQuotationMod() == null || responseQuotation.getModality() == null){
			this.addAdviceWithDescription(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getAdviceCode(),
					RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getMessage());
			return null;
		}

		IQuotationDAO quotationDAO = new QuotationDAOImpl(this.pisdR402);
		BigDecimal insuranceProductId = ConvertUtils.getBigDecimalValue(responseProductMap.get(
				ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_PRODUCT_ID));
		String modalityType = responseQuotation.getQuotationMod().getInsuranceModalityType();

		Map<String, Object> employeeInfoMap = quotationDAO.getEmployeesInfoFromDB(quotationId, insuranceProductId,
				modalityType);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | employeeInfo: {}",employeeInfoMap);

		Map<String,Object> paymentDetailsMap = quotationDAO.getPaymentDetailsByQuotationFromDB(quotationId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | paymentDetailsMap: {}",paymentDetailsMap);

		String quotationReference = responseQuotation.getQuotation().getRfqInternalId();

		ResponseQuotationDetailBO responseRimac = callRimacService(responseProductMap,quotationReference,traceId);
		LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseRimac: {}",responseRimac);

		if(responseRimac == null || responseRimac.getPayload() == null || responseRimac.getPayload().getPlan() == null){
			this.addAdviceWithDescription(RBVDErrors.ERROR_CALL_QUOTATION_DETAIL_API.getAdviceCode(),
					RBVDErrors.ERROR_CALL_QUOTATION_DETAIL_API.getMessage());
			return null;
		}

		response.setId(quotationId);
		response.setQuotationDate(ConvertUtils.convertStringDateToLocalDate(responseQuotation.getQuotation().getQuoteDate()));

		IEmployeesBusiness employeesBusiness = new EmployeesBusinessImpl();
		response.setEmployees(employeesBusiness.constructEmployeesInfo(employeeInfoMap));

		IProductBusiness productBusiness = new ProductBusinessImpl(this.applicationConfigurationService);
		response.setProduct(productBusiness.constructProductInfo(responseRimac.getPayload(),responseQuotation));

		IContactDetailsBusiness contactDetailsBusiness = new ContactDetailsBusinessImpl();
		response.setContactDetails(contactDetailsBusiness.constructContactDetailsInfo(responseQuotation.getQuotationMod()));

		response.setValidityPeriod(createValidityPeriodDTO(responseRimac.getPayload().getPlan()));
		response.setBusinessAgent(createBusinessAgentDTO(responseQuotation.getQuotation().getUserAuditId()));

		IParticipantsBusiness participantsBusiness = new PariticipantsBusinessImpl(this.applicationConfigurationService);
		response.setParticipants(participantsBusiness.constructParticipantsInfo(responseQuotation.getQuotation()));

		response.setQuotationReference(quotationReference);
		response.setStatus(null);

		if(ValidateUtils.mapIsNullOrEmpty(paymentDetailsMap)){
			response.setPaymentMethod(null);
			response.setBank(null);
		}else{
			IPaymentBusiness paymentBusiness = new PaymentBusinessImpl(paymentDetailsMap,this.applicationConfigurationService);
			response.setPaymentMethod(paymentBusiness.constructPaymentMethodInfo());
			response.setBank(paymentBusiness.constructBankInfo());
		}

		return response;
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

		ConsumerExternalService consumerExternalService = new ConsumerExternalService();
		consumerExternalService.setExternalApiConnector(this.externalApiConnector);
		consumerExternalService.setApplicationConfigurationService(this.applicationConfigurationService);
		consumerExternalService.setPisdR014(this.pisdR014);

		return consumerExternalService.executeQuotationDetailRimac(inputRimac);
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

	private ValidityPeriodDTO createValidityPeriodDTO(PlanBO planRimac){
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
	}

}
