package com.bbva.rbvd.lib.r407.impl.service.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface IQuotationDAO {

    Map<String, Object> getEmployeesInfoFromDB(String quotationId, BigDecimal insuranceProductId, String modalityType);

    Map<String,Object> getPaymentDetailsByQuotationFromDB(String quotationId);
}
