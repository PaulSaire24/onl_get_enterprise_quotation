package com.bbva.rbvd.lib.r407.impl.transform.map;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationInputDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QuotationMap {

    private QuotationMap(){}

    public static Map<String,Object> argumentUpdatePremiumAmount(QuotationInputDTO input, BigDecimal insuranceProductId, String modalityType, BigDecimal amount) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID,input.getQuotationId());
        arguments.put(ConstantsUtil.QuotationModMap.INSURANCE_PRODUCT_ID,insuranceProductId);
        arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE,modalityType);
        arguments.put(ConstantsUtil.QuotationModMap.PREMIUM_AMOUNT,amount);
        arguments.put(ConstantsUtil.QuotationMap.USER_AUDIT_ID,input.getTransactionCode());

        return arguments;
    }

}
