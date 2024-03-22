package com.bbva.rbvd.lib.r407.impl.service.dao.impl;

import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.service.dao.IProductDAO;

import java.util.HashMap;
import java.util.Map;

public class ProductDAOImpl implements IProductDAO {

    private final PISDR401 pisdR401;

    public ProductDAOImpl(PISDR401 pisdR401) {
        this.pisdR401 = pisdR401;
    }

    @Override
    public Map<String, Object> getProductInformation(String quotationId) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
        return (Map<String,Object>) this.pisdR401.executeGetProductById(
                ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC,arguments);
    }
}
