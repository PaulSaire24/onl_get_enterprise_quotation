package com.bbva.rbvd.lib.r407.impl;


import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ValidityPeriodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.PlanBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.InsrncParticipantDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.ProductDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationInputDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.InputQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IEmployeesBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IParticipantsBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IProductBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IContactDetailsBusiness;
import com.bbva.rbvd.lib.r407.impl.business.IPaymentBusiness;
import com.bbva.rbvd.lib.r407.impl.business.impl.EmployeesBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.PariticipantsBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.PaymentBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.ProductBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.business.impl.ContactDetailsBusinessImpl;
import com.bbva.rbvd.lib.r407.impl.service.api.ConsumerExternalService;
import com.bbva.rbvd.lib.r407.impl.service.dao.IParticipantsDAO;
import com.bbva.rbvd.lib.r407.impl.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r407.impl.service.dao.IQuotationDAO;
import com.bbva.rbvd.lib.r407.impl.service.dao.impl.ParticipantsDAOImpl;
import com.bbva.rbvd.lib.r407.impl.service.dao.impl.ProductDAOImpl;
import com.bbva.rbvd.lib.r407.impl.service.dao.impl.QuotationDAOImpl;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


/**
 * The RBVDR407Impl class...
 */
public class RBVDR407Impl extends RBVDR407Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR407Impl.class);

	@Override
	public EnterpriseQuotationDTO executeGetQuotationLogic(QuotationInputDTO input) {

		try{
			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() - START");

			EnterpriseQuotationDTO response = new EnterpriseQuotationDTO();

			IProductDAO productDAO = new ProductDAOImpl(this.pisdR401);
			ProductDAO responseProduct = productDAO.getProductInformation(input.getQuotationId());
			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseProduct: {}",responseProduct);

			IQuotationDAO quotationDAO = new QuotationDAOImpl(this.pisdR402);
			QuotationDAO responseQuotation = quotationDAO.getQuotationDetailByQuotationId(input.getQuotationId());
			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseQuotation: {}",responseQuotation);

			String quotationReference = responseQuotation.getRfqInternalId();

			PaymentDAO paymentDetails = quotationDAO.getPaymentDetailsByQuotationId(input.getQuotationId());
			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | paymentDetails: {}",paymentDetails);

			List<InsrncParticipantDAO> participantsFromDB = null;
			if(paymentDetails != null && ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(paymentDetails.getEntity(),
					paymentDetails.getBranch(),paymentDetails.getAccountId()))){
				IParticipantsDAO participantsDAO = new ParticipantsDAOImpl(this.pisdR402);
				participantsFromDB = participantsDAO.getParticipantsByContract(paymentDetails.getEntity(),paymentDetails.getBranch(),paymentDetails.getAccountId());
			}

			ResponseQuotationDetailBO responseRimac = callRimacService(responseProduct,quotationReference,input.getTraceId());
			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | responseRimac: {}",responseRimac);

			response.setId(input.getQuotationId());
			response.setQuotationDate(ConvertUtils.convertStringDateToLocalDate(responseQuotation.getQuoteDate()));

			IEmployeesBusiness employeesBusiness = new EmployeesBusinessImpl();
			response.setEmployees(employeesBusiness.constructEmployees(responseQuotation));

			IProductBusiness productBusiness = new ProductBusinessImpl(this.applicationConfigurationService);
			response.setProduct(productBusiness.constructProduct(responseRimac.getPayload(),responseQuotation));

			IContactDetailsBusiness contactDetailsBusiness = new ContactDetailsBusinessImpl();
			response.setContactDetails(contactDetailsBusiness.constructContactDetails(
					responseQuotation.getContactEmailDesc(),responseQuotation.getCustomerPhoneDesc()));

			response.setValidityPeriod(createValidityPeriodDTO(responseRimac.getPayload().getPlan()));
			response.setBusinessAgent(createBusinessAgentDTO(responseQuotation.getUserAuditId()));

			IParticipantsBusiness participantsBusiness = new PariticipantsBusinessImpl(this.applicationConfigurationService);
			response.setParticipants(participantsBusiness.constructParticipants(responseQuotation,participantsFromDB));

			response.setQuotationReference(quotationReference);
			response.setStatus(null);

			if(paymentDetails == null){
				response.setPaymentMethod(null);
				response.setBank(null);
				response.setInsuredAmount(null);
			}else{
				IPaymentBusiness paymentBusiness = new PaymentBusinessImpl(this.applicationConfigurationService);
				response.setPaymentMethod(paymentBusiness.constructPaymentMethod(paymentDetails));
				response.setBank(paymentBusiness.constructBank(paymentDetails.getEntity(),paymentDetails.getBranch()));
				response.setInsuredAmount(paymentBusiness.constructInsuredAmount(paymentDetails.getInsuredAmount(),paymentDetails.getCurrency()));
			}

			//Actualizar monto de prima real
			String quotationType = getQuotationTypeByQuoteReference(quotationReference);
			BigDecimal amount = responseRimac.getPayload().getPlan().getPrimaBruta();
			if(quotationType.equals(ConstantsUtil.StringConstants.C) && amount != null){
				int updateAmountResult = quotationDAO.updatePremiumAmount(
						input,
						responseProduct.getInsuranceProductId(),
						responseQuotation.getInsuranceModalityType(),
						amount
				);
				LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | updateAmountResult: {}",updateAmountResult);
			}

			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() | response: {}",response);
			LOGGER.info("RBVDR407Impl - executeGetQuotationLogic() - END");
			return response;
		}catch (BusinessException bex){
			this.addAdviceWithDescription(bex.getAdviceCode(),bex.getMessage());
			return null;
		}
	}

	private ResponseQuotationDetailBO callRimacService(ProductDAO responseProduct,String quotationReference,
													   String traceId){
		String externalQuotationId = responseProduct.getInsuranceCompanyQuotaId();
		String productShortDesc = responseProduct.getProductShortDesc();

		String quotationType = getQuotationTypeByQuoteReference(quotationReference);

		InputQuotationDetailBO inputRimac = new InputQuotationDetailBO();
		inputRimac.setCotizacion(externalQuotationId);
		inputRimac.setProducto(productShortDesc);
		inputRimac.setTipoCotizacion(quotationType);
		inputRimac.setTraceId(traceId);

		ConsumerExternalService consumerExternalService = new ConsumerExternalService();
		consumerExternalService.setExternalApiConnector(this.externalApiConnector);
		consumerExternalService.setApplicationConfigurationService(this.applicationConfigurationService);
		consumerExternalService.setPisdR014(this.pisdR014);

		ResponseQuotationDetailBO responseRimac = consumerExternalService.executeQuotationDetailRimac(inputRimac);

		if(responseRimac == null || responseRimac.getPayload() == null || responseRimac.getPayload().getPlan() == null){
			throw new BusinessException("RBVD00000174",false,"Hubo un error al llamar al api de Detalle Cotización de Rimac");
		}

		return responseRimac;
	}

	private static String getQuotationTypeByQuoteReference(String quotationReference) {
		String quotationType;

		if(ValidateUtils.stringIsNullOrEmpty(quotationReference)){
			quotationType = ConstantsUtil.StringConstants.R;
		}else{
			quotationType = ConstantsUtil.StringConstants.C;
		}

		return quotationType;
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

		if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(fechaFin,fechaInicio))){
			ValidityPeriodDTO validityPeriod = new ValidityPeriodDTO();
			validityPeriod.setStartDate(ConvertUtils.convertStringDateToDate(fechaInicio));
			validityPeriod.setEndDate(ConvertUtils.convertStringDateToDate(fechaFin));

			return validityPeriod;
		}else{
			return null;
		}
	}

}
