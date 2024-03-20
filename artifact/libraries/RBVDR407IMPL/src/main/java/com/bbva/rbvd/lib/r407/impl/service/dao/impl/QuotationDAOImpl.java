package com.bbva.rbvd.lib.r407.impl.service.dao.impl;

import com.bbva.pisd.lib.r402.PISDR402;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.service.dao.IQuotationDAO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QuotationDAOImpl implements IQuotationDAO {

    private final PISDR402 pisdR402;

    public QuotationDAOImpl(PISDR402 pisdR402) {
        this.pisdR402 = pisdR402;
    }

    @Override
    public Map<String, Object> getEmployeesInfoFromDB(String quotationId, BigDecimal insuranceProductId, String modalityType) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
        arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_PRODUCT_ID,insuranceProductId);
        arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE, modalityType);
        return this.pisdR402.executeGetASingleRow(
                ConstantsUtil.QueriesName.QUERY_FIND_ENTERPRISE_EMPLOYEE_FROM_QUOTATION,arguments);
    }

    @Override
    public Map<String, Object> getPaymentDetailsByQuotationFromDB(String quotationId) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
        return this.pisdR402.executeGetASingleRow(
                ConstantsUtil.QueriesName.QUERY_FIND_PAYMENTMETHOD_FROM_QUOTATION,arguments);
    }

}
