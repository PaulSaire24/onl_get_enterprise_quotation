package com.bbva.rbvd.lib.r407.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.lib.r407.utils.ContansUtils;

public class ProductDAO {

    public ProductDTO getProductInfo(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId("403");
        productDTO.setName(ContansUtils.mockInternalData.PRODUCT_NAME);
        return productDTO;
    }
}
