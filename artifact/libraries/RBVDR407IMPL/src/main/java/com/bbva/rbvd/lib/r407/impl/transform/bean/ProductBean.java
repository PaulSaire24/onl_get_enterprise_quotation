package com.bbva.rbvd.lib.r407.impl.transform.bean;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.ProductDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;

import java.util.Map;

public class ProductBean {

    private ProductBean(){}

    public static ProductDAO mapProductDataToBean(Map<String,Object> responseMap){
        ProductDAO productDAO = new ProductDAO();

        productDAO.setInsuranceProductId(ConvertUtils.getBigDecimalValue(responseMap.get(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_PRODUCT_ID)));
        productDAO.setProductShortDesc((String) responseMap.get(ConstantsUtil.InsuranceProduct.FIELD_PRODUCT_SHORT_DESC));
        productDAO.setInsuranceBusinessName((String) responseMap.get(ConstantsUtil.InsuranceProduct.FIELD_INSURANCE_PRODUCT_BUSINESS_NAME));
        productDAO.setInsuranceCompanyQuotaId((String) responseMap.get(ConstantsUtil.QuotationMap.INSURANCE_COMPANY_QUOTA_ID));

        return productDAO;
    }

}
