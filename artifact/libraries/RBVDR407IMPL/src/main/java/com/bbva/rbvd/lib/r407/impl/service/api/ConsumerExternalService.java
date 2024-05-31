package com.bbva.rbvd.lib.r407.impl.service.api;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.InputQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConsumerExternalService {

    private ApplicationConfigurationService applicationConfigurationService;
    private PISDR014 pisdR014;
    private APIConnector externalApiConnector;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerExternalService.class);

    public ResponseQuotationDetailBO executeQuotationDetailRimac(InputQuotationDetailBO params){

        LOGGER.info("RBVDR407Impl - executeQuotationDetailRimac() | input params: {}", params);

        String externalQuotationId = params.getCotizacion();
        String productName = params.getProducto();
        String quotationType = params.getTipoCotizacion();
        ResponseEntity<ResponseQuotationDetailBO> rimacResponse = null;
        ResponseQuotationDetailBO rimacResponseBody = null;

        String uri = this.applicationConfigurationService.getProperty(ConstantsUtil.QuotationDetailRimac.KEY_URI_FROM_CONSOLE);
        uri = uri.replace(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_EXTERNAL_QUOTATION_ID,externalQuotationId)
                .replace(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_PRODUCT_NAME,productName);
        String queryString = ConstantsUtil.QuotationDetailRimac.QUERY_STRING_TIPO_COTIZACION + quotationType;

        SignatureAWS signatureAws = this.pisdR014.executeSignatureConstruction(null, javax.ws.rs.HttpMethod.GET, uri,
                queryString, params.getTraceId());
        HttpEntity<String> entity = new HttpEntity<>(createHttpHeadersAWS(signatureAws));

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_EXTERNAL_QUOTATION_ID, externalQuotationId);
        pathParams.put(ConstantsUtil.QuotationDetailRimac.PATH_PARAM_PRODUCT_NAME, productName);
        pathParams.put(ConstantsUtil.QuotationDetailRimac.QUERY_PARAM_QUOTATION_TYPE,quotationType);

        try {
            rimacResponse = this.externalApiConnector.exchange(ConstantsUtil.QuotationDetailRimac.KEY_RIMAC_SERVICE,
                    HttpMethod.GET, entity,ResponseQuotationDetailBO.class,pathParams);
            rimacResponseBody = rimacResponse.getBody();

            LOGGER.info("RBVDR407Impl - executeQuotationDetailRimac() | response rimac body: {}", rimacResponseBody);
            return rimacResponseBody;
        }catch (RestClientException ex){
            LOGGER.error("RBVDR407Impl - executeQuotationDetailRimac() | RestClientException message {}",ex.getMessage());
            return null;
        }
    }

    private HttpHeaders createHttpHeadersAWS(final SignatureAWS signature) {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        headers.set(ConstantsUtil.HeaderSignatureAWS.AUTHORIZATION, signature.getAuthorization());
        headers.set(ConstantsUtil.HeaderSignatureAWS.X_AMZ_DATE, signature.getxAmzDate());
        headers.set(ConstantsUtil.HeaderSignatureAWS.X_API_KEY, signature.getxApiKey());
        headers.set(ConstantsUtil.HeaderSignatureAWS.TRACEID, signature.getTraceId());
        return headers;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setPisdR014(PISDR014 pisdR014) {
        this.pisdR014 = pisdR014;
    }

    public void setExternalApiConnector(APIConnector externalApiConnector) {
        this.externalApiConnector = externalApiConnector;
    }
}
