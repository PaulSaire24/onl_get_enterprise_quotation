package com.bbva.rbvd.lib.r407.impl;

import com.bbva.rbvd.dto.enterpriseinsurance.createquotation.rimac.QuotationResponseBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.GetQuotationDTO;
import com.bbva.rbvd.lib.r407.service.impl.ConsumerExternalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bbva.rbvd.lib.r407.transform.bean.QuotationRimac.mapInQuotationResponse;

/**
 * The RBVDR407Impl class...
 */
public class RBVDR407Impl extends RBVDR407Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR407Impl.class);

	/**
	 * The execute method...
	 */
	@Override
	public GetQuotationDTO executeGetQuotation(String quotationId) {
		GetQuotationDTO response;
		GetQuotationDTO mockRimac = new GetQuotationDTO();
		ConsumerExternalService consumerExternalService = new ConsumerExternalService();
		QuotationResponseBO rimacInput = new QuotationResponseBO();
		QuotationResponseBO responseRimac = consumerExternalService.callRimacService(rimacInput);
		response = mapInQuotationResponse(mockRimac, quotationId);

		return response;
	}
}
