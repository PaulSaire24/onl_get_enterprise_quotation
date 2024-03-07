package com.bbva.rbvd.lib.r407;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationDetailDTO;

/**
 * The  interface RBVDR407 class...
 */
public interface RBVDR407 {

	/**
	 * The executeGetQuotationLogic method...
	 */
	EnterpriseQuotationDTO executeGetQuotationLogic(String quotationId, String traceId);

}
