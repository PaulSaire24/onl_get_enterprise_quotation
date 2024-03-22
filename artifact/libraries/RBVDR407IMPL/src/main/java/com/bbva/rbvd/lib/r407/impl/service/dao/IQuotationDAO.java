package com.bbva.rbvd.lib.r407.impl.service.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface IQuotationDAO {

    Map<String,Object> getQuotationDetailByQuotationId(String quotationId);

    Map<String, Object> getEmployeesData(String quotationId, BigDecimal insuranceProductId, String modalityType);

    Map<String,Object> getPaymentDetailsByQuotationId(String quotationId);
}
