package com.bbva.rbvd.lib.r407;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;

/**
 * The  interface RBVDR407 class...
 */
public interface RBVDR407 {

	/**
	 * The executeGetQuotationLogic method...
	 */
	EnterpriseQuotationDTO executeGetQuotationLogic(String quotationId, String traceId,String transactionCode);

}
