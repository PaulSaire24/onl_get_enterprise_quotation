package com.bbva.rbvd.lib.r407.impl.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.ProductDAO;


public interface IProductDAO {

    ProductDAO getProductInformation(String quotationId);
}
