package com.bbva.rbvd.lib.r407;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;


import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.*;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationDetailDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.mock.MockData;
import com.bbva.rbvd.lib.r407.impl.RBVDR407Impl;

import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RBVDR407Test {

	@Spy
	private Context context;

	private RBVDR407Impl rbvdR407Impl = new RBVDR407Impl();


	private ApplicationConfigurationService applicationConfigurationService;
	private APIConnector externalApiConnector;
	private PISDR401 pisdr401;
	private PISDR014 pisdr014;
	private final String quotationId = "081400000381";
	private ResponseQuotationDetailBO responseRimac;
	Map<String, Object> mapProductInfo = new HashMap<>();


	@Before
	public void setUp() throws IOException {
		context = new Context();
		ThreadContext.set(context);

		pisdr401 = mock(PISDR401.class);
		pisdr014 = mock(PISDR014.class);
		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		externalApiConnector = mock(APIConnector.class);

		rbvdR407Impl.setPisdR401(pisdr401);
		rbvdR407Impl.setPisdR014(pisdr014);
		rbvdR407Impl.setApplicationConfigurationService(applicationConfigurationService);
		rbvdR407Impl.setExternalApiConnector(externalApiConnector);

		MockData mockData = MockData.getInstance();

		responseRimac = mockData.getResponseQuotationDetailRimac();

		mapProductInfo.put("INSURANCE_COMPANY_QUOTA_ID","1cdd6ec3-67eb-443a-b4e6-ff10257cf205");
		mapProductInfo.put("PRODUCT_SHORT_DESC","VIDALEY");
		mapProductInfo.put("INSURANCE_BUSINESS_NAME","VIDA");
		mapProductInfo.put("INSURANCE_PRODUCT_ID",new BigDecimal("13"));
		Mockito.when(this.pisdr401.executeGetProductById(
				"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",
						Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID",quotationId)))
				.thenReturn(mapProductInfo);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(),Mockito.any(HttpMethod.class),Mockito.any(),
				(Class<ResponseQuotationDetailBO>)Mockito.any(),Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		Mockito.when(this.applicationConfigurationService.getProperty("rimac.quotationdetail.enterprise.uri"))
				.thenReturn("/api-vida/V1/cotizaciones/externalQuotationId/producto/productName/detalle");
		Mockito.when(this.applicationConfigurationService.getProperty("OBL_COVERAGE_ID")).thenReturn("MANDATORY");
		Mockito.when(this.applicationConfigurationService.getProperty("OBL_COVERAGE_NAME")).thenReturn("OBLIGATORIA");

		Mockito.when(this.pisdr014.executeSignatureConstruction(any(), any(), anyString(), any(), anyString()))
				.thenReturn(new SignatureAWS("authorization","xAmzDate","xApiKey","traceId"));

	}

	@Test
	public void executeGetQuotationLogic_TestOK(){

		QuotationDetailDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNotNull(response);

		Assert.assertNotNull(response.getId());

		Assert.assertNotNull(response.getQuotationDate());

		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getId());
		Assert.assertNotNull(response.getProduct().getName());
		Assert.assertEquals(responseRimac.getPayload().getProducto(),response.getProduct().getName());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertEquals(1,response.getProduct().getPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertEquals(responseRimac.getPayload().getPlan().getDescripcionPlan(),
				response.getProduct().getPlans().get(0).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getIsSelected());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getTotalInstallment());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getInstallmentPlans());
		Assert.assertEquals(1,response.getProduct().getPlans().get(0).getInstallmentPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages());
		Assert.assertEquals(3,response.getProduct().getPlans().get(0).getCoverages().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(2).getDescription());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getBenefits());
		Assert.assertNull(response.getProduct().getPlans().get(0).getExclusions());

		Assert.assertNotNull(response.getValidityPeriod());
		Assert.assertNotNull(response.getValidityPeriod().getStartDate());
		Assert.assertNotNull(response.getValidityPeriod().getEndDate());
		Assert.assertEquals(ConvertUtils.convertStringDateToDate(responseRimac.getPayload().getPlan().getFechaInicio()),
				response.getValidityPeriod().getStartDate());
		Assert.assertEquals(ConvertUtils.convertStringDateToDate(responseRimac.getPayload().getPlan().getFechaFin()),
				response.getValidityPeriod().getEndDate());

	}

	@Test
	public void executeGetQuotationLogic_QuotationNotExist(){
		mapProductInfo = null;
		Mockito.when(this.pisdr401.executeGetProductById(
						"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",
						Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID",quotationId)))
				.thenReturn(mapProductInfo);

		QuotationDetailDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1,context.getAdviceList().size());
		Assert.assertEquals("RBVD00000129",context.getAdviceList().get(0).getCode());
	}

	@Test
	public void executeGetQuotationLogic_WithRimacNull(){
		responseRimac = null;
		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(),Mockito.any(HttpMethod.class),Mockito.any(),
				(Class<ResponseQuotationDetailBO>)Mockito.any(),Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.BAD_REQUEST)
		);

		QuotationDetailDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1,context.getAdviceList().size());
		Assert.assertEquals("RBVD00000174",context.getAdviceList().get(0).getCode());
	}


	@Test
	public void executeGetQuotationLogic_WithNotPlanInRimacResponse(){
		responseRimac.getPayload().setPlan(null);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(),Mockito.any(HttpMethod.class),Mockito.any(),
				(Class<ResponseQuotationDetailBO>)Mockito.any(),Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		QuotationDetailDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getProduct());
		Assert.assertEquals(0,response.getProduct().getPlans().size());
		Assert.assertNull(response.getValidityPeriod());
	}

	@Test
	public void executeGetQuotationLogic_WithBenefitsRimacIsNull(){
		responseRimac.getPayload().getPlan().setAsistencias(null);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(),Mockito.any(HttpMethod.class),Mockito.any(),
				(Class<ResponseQuotationDetailBO>)Mockito.any(),Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		QuotationDetailDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertEquals(0,response.getProduct().getPlans().get(0).getBenefits().size());
		Assert.assertEquals(3,response.getProduct().getPlans().get(0).getCoverages().size());
	}

	@Test
	public void executeGetQuotationLogic_WithCoveragesRimacIsNull(){
		responseRimac.getPayload().getPlan().setCoberturas(null);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(),Mockito.any(HttpMethod.class),Mockito.any(),
				(Class<ResponseQuotationDetailBO>)Mockito.any(),Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		QuotationDetailDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertEquals(3,response.getProduct().getPlans().get(0).getBenefits().size());
		Assert.assertEquals(0,response.getProduct().getPlans().get(0).getCoverages().size());
	}


	
}
