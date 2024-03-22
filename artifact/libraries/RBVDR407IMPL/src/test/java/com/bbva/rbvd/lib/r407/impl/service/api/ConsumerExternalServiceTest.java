package com.bbva.rbvd.lib.r407.impl.service.api;


import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.InputQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.lib.r407.factory.ApiConnectorFactoryMock;
import com.bbva.rbvd.mock.MockBundleContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsumerExternalServiceTest {

    private ApplicationConfigurationService applicationConfigurationService;
    private PISDR014 pisdr014;
    private APIConnector externalApiConnector;

    ConsumerExternalService consumerExternalService = new ConsumerExternalService();


    @Before
    public void setUp() {
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        pisdr014 = mock(PISDR014.class);

        MockBundleContext mockBundleContext = mock(MockBundleContext.class);
        ApiConnectorFactoryMock apiConnectorFactoryMock = new ApiConnectorFactoryMock();
        externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);

        consumerExternalService.setExternalApiConnector(externalApiConnector);
        consumerExternalService.setPisdR014(pisdr014);
        consumerExternalService.setApplicationConfigurationService(applicationConfigurationService);

        when(this.applicationConfigurationService.getProperty("rimac.quotationdetail.enterprise.uri"))
                .thenReturn("/api-vida/V1/cotizaciones/externalQuotationId/producto/productName/detalle");

        when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new SignatureAWS("", "", "", ""));

    }

    @Test
    public void testExecuteQuotationDetailRimac_ErrorRestClient() {
        when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.anyObject(), Mockito.anyObject(),
                (Class<ResponseQuotationDetailBO>) any(), Mockito.anyMap()))
                .thenThrow(new RestClientException("Error al obtener detalle de cotizacion"));

        InputQuotationDetailBO inputQuotationDetailBO = new InputQuotationDetailBO();
        inputQuotationDetailBO.setCotizacion("ce21ceb0-5890-4b7a-811f-90d165358fd6");
        inputQuotationDetailBO.setProducto("VIDALEY");
        inputQuotationDetailBO.setTipoCotizacion("R");

        ResponseQuotationDetailBO responseRimac = consumerExternalService.executeQuotationDetailRimac(inputQuotationDetailBO);

        Assert.assertNull(responseRimac);
    }


}