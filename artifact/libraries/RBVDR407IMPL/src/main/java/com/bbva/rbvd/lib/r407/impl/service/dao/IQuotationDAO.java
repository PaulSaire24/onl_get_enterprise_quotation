package com.bbva.rbvd.lib.r407.impl.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationInputDTO;

import java.math.BigDecimal;

public interface IQuotationDAO {

    QuotationDAO getQuotationDetailByQuotationId(String quotationId);

    PaymentDAO getPaymentDetailsByQuotationId(String quotation);

    int updatePremiumAmount(QuotationInputDTO input, BigDecimal insuranceProductId, String modalityType, BigDecimal amount);
}
