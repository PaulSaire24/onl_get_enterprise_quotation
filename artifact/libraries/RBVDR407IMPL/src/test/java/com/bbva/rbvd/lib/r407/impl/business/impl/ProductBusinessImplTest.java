package com.bbva.rbvd.lib.r407.impl.business.impl;


import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.mock.MockData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;


public class ProductBusinessImplTest {

    private ApplicationConfigurationService applicationConfigurationService;

    private ProductBusinessImpl productBusiness;
    private ResponseQuotationDetailBO responseRimac;
    private QuotationDAO responseQuotation;

    @Before
    public void setUp() throws IOException {
        applicationConfigurationService = Mockito.mock(ApplicationConfigurationService.class);
        productBusiness = new ProductBusinessImpl(applicationConfigurationService);

        MockData mockData = MockData.getInstance();

        responseRimac = mockData.getResponseQuotationDetailRimac();
        responseRimac.getPayload().getPlan().getCoberturas().get(0).setPrincipal("S");

        responseQuotation = new QuotationDAO();
        responseQuotation.setInsuranceProductType("11");
        responseQuotation.setInsuranceModalityType("01");
        responseQuotation.setInsuranceModalityName("PLAN 1");

        Mockito.when(applicationConfigurationService.getProperty("COVERAGE_TYPE_OBL")).thenReturn("MAIN");
        Mockito.when(applicationConfigurationService.getProperty("COVERAGE_TYPE_OPC")).thenReturn("ADDITIONAL");
        Mockito.when(applicationConfigurationService.getProperty("COVERAGE_TYPE_INC")).thenReturn("INCLUDED");
        Mockito.when(applicationConfigurationService.getProperty("OBL_COVERAGE_NAME")).thenReturn("OBLIGATORIA");
        Mockito.when(applicationConfigurationService.getProperty("OPC_COVERAGE_NAME")).thenReturn("OPCIONAL");
        Mockito.when(applicationConfigurationService.getProperty("INC_COVERAGE_NAME")).thenReturn("INCLUIDA");
    }

    @Test
    public void testAmountNull(){
        responseRimac.getPayload().getPlan().setPrimaBruta(null);
        responseRimac.getPayload().getPlan().getFinanciamientos().get(0).setCuotasFinanciamiento(null);

        ProductDTO constructProduct = productBusiness.constructProduct(responseRimac.getPayload(), responseQuotation);

        Assert.assertNotNull(constructProduct);
        Assert.assertNull(constructProduct.getPlans().get(0).getTotalInstallment());
        Assert.assertNull(constructProduct.getPlans().get(0).getInstallmentPlans().get(0).getPaymentAmount());

    }

    @Test
    public void testCoveragesAndBenefitsNull(){
        responseRimac.getPayload().getPlan().setCoberturas(null);
        responseRimac.getPayload().getPlan().setAsistencias(null);

        ProductDTO constructProduct = productBusiness.constructProduct(responseRimac.getPayload(), responseQuotation);

        Assert.assertNotNull(constructProduct);
        Assert.assertTrue(constructProduct.getPlans().get(0).getCoverages().isEmpty());
        Assert.assertTrue(constructProduct.getPlans().get(0).getBenefits().isEmpty());
    }

    @Test
    public void testFinancingNull(){
        responseRimac.getPayload().getPlan().setFinanciamientos(null);

        ProductDTO constructProduct = productBusiness.constructProduct(responseRimac.getPayload(), responseQuotation);

        Assert.assertNotNull(constructProduct);
        Assert.assertEquals(0,constructProduct.getPlans().get(0).getInstallmentPlans().size());
    }

}