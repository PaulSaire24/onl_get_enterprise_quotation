package com.bbva.rbvd.lib.r407.impl.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.ProductDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r407.impl.transform.bean.ProductBean;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.util.HashMap;
import java.util.Map;

public class ProductDAOImpl implements IProductDAO {

    private final PISDR401 pisdR401;

    public ProductDAOImpl(PISDR401 pisdR401) {
        this.pisdR401 = pisdR401;
    }

    @Override
    public ProductDAO getProductInformation(String quotationId) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);

        Map<String,Object> responseMap = (Map<String,Object>) this.pisdR401.executeGetProductById(ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC,arguments);

        if(ValidateUtils.mapIsNullOrEmpty(responseMap)){
            throw new BusinessException("RBVD01020092",false,"No se encontró datos del producto de la cotización");
        }

        return ProductBean.mapProductDataToBean(responseMap);
    }
}
