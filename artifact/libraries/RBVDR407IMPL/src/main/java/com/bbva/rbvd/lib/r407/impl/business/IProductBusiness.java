package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.pisd.dto.insurancedao.join.QuotationJoinQuotationModDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;

public interface IProductBusiness {

    ProductDTO constructProductInfo(ResponsePayloadQuotationDetailBO payload,
                                    QuotationJoinQuotationModDTO responseQuotation);

}
