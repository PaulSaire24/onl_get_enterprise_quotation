package com.bbva.rbvd.lib.r407;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.pisd.lib.r402.PISDR402;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationInputDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.mock.MockData;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.RBVDR407Impl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR407-app.xml",
		"classpath:/META-INF/spring/RBVDR407-app-test.xml",
		"classpath:/META-INF/spring/RBVDR407-arc.xml",
		"classpath:/META-INF/spring/RBVDR407-arc-test.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RBVDR407Test {

	@Spy
	private Context context;

	private final RBVDR407Impl rbvdR407Impl = new RBVDR407Impl();

	private APIConnector externalApiConnector;
	private PISDR401 pisdr401;

	private PISDR402 pisdr402;
	private final String quotationId = "01728424224300";
	private ResponseQuotationDetailBO responseRimac;
	private Map<String, Object> mapProductInfo = new HashMap<>();
	private Map<String,Object> policyQuotaInternalId;
	private QuotationInputDTO input;
	private MockData mockData;


	@Before
	public void setUp() throws IOException {
		context = new Context();
		ThreadContext.set(context);

		pisdr401 = mock(PISDR401.class);
		PISDR014 pisdr014 = mock(PISDR014.class);
		pisdr402 = mock(PISDR402.class);
		ApplicationConfigurationService applicationConfigurationService = mock(ApplicationConfigurationService.class);
		externalApiConnector = mock(APIConnector.class);

		rbvdR407Impl.setPisdR401(pisdr401);
		rbvdR407Impl.setPisdR014(pisdr014);
		rbvdR407Impl.setPisdR402(pisdr402);
		rbvdR407Impl.setApplicationConfigurationService(applicationConfigurationService);
		rbvdR407Impl.setExternalApiConnector(externalApiConnector);

		policyQuotaInternalId = Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID", quotationId);

		mockData = MockData.getInstance();

		responseRimac = mockData.getResponseQuotationDetailRimac();

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		input = new QuotationInputDTO();
		input.setQuotationId(quotationId);
		String transactionCode = "RBVDT404";
		input.setTransactionCode(transactionCode);
		input.setTraceId("traceId");

		Mockito.when(applicationConfigurationService.getProperty("rimac.quotationdetail.enterprise.uri"))
				.thenReturn("/api-vida/V1/cotizaciones/externalQuotationId/producto/productName/detalle");
		Mockito.when(applicationConfigurationService.getProperty("COVERAGE_TYPE_OBL")).thenReturn("MAIN");
		Mockito.when(applicationConfigurationService.getProperty("COVERAGE_TYPE_OPC")).thenReturn("ADDITIONAL");
		Mockito.when(applicationConfigurationService.getProperty("COVERAGE_TYPE_INC")).thenReturn("INCLUDED");
		Mockito.when(applicationConfigurationService.getProperty("OBL_COVERAGE_NAME")).thenReturn("OBLIGATORIA");
		Mockito.when(applicationConfigurationService.getProperty("OPC_COVERAGE_NAME")).thenReturn("OPCIONAL");
		Mockito.when(applicationConfigurationService.getProperty("INC_COVERAGE_NAME")).thenReturn("INCLUIDA");
		Mockito.when(applicationConfigurationService.getProperty("R")).thenReturn("RUC");
		Mockito.when(applicationConfigurationService.getProperty("Mensual")).thenReturn("MONTHLY");
		Mockito.when(applicationConfigurationService.getProperty("Anual")).thenReturn("ANNUAL");

		Mockito.when(pisdr014.executeSignatureConstruction(any(), any(), anyString(), any(), anyString()))
				.thenReturn(new SignatureAWS("authorization", "xAmzDate", "xApiKey", "traceId"));

		mapProductInfo.put("INSURANCE_COMPANY_QUOTA_ID", "6e4e94b6-56ba-41e7-9e22-1b264898c1cc");
		mapProductInfo.put("PRODUCT_SHORT_DESC", "VIDALEY");
		mapProductInfo.put("INSURANCE_BUSINESS_NAME", "VIDA");
		mapProductInfo.put("INSURANCE_PRODUCT_ID", 13);
		Mockito.when(pisdr401.executeGetProductById(ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC, policyQuotaInternalId))
				.thenReturn(mapProductInfo);

		Map<String,Object> paymentMap = dataPaymentByQuotationId();
		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID", policyQuotaInternalId))
				.thenReturn(paymentMap);

		Map<String,Object> quotatioNDetailMap = dataQuotationDetailByQuotationId();
		Mockito.when(pisdr402.executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId))
				.thenReturn(quotatioNDetailMap);

	}

	private Map<String,Object> dataQuotationDetailByQuotationId() {
		Map<String,Object> quotationMap = new HashMap<>();

		quotationMap.put(ConstantsUtil.QuotationMap.QUOTE_DATE, "2024-05-20");
		quotationMap.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE, "01");
		quotationMap.put(ConstantsUtil.InsuranceProduct.FIELD_INSURANCE_PRODUCT_TYPE, "842");
		quotationMap.put("INSURANCE_MODALITY_NAME", "PLAN BASE");
		quotationMap.put("INSUR_MODALITY_DESC", "PLAN 01 VIDA LEY");
		quotationMap.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_COMPANY_MODALITY_ID, "534254");
		quotationMap.put(ConstantsUtil.QuotationMap.USER_AUDIT_ID, "ZG13002");
		quotationMap.put(ConstantsUtil.QuotationMap.CUSTOMER_ID, "90006083");
		quotationMap.put(ConstantsUtil.QuotationMap.FIELD_POLICY_QUOTA_STATUS_TYPE,"COT");
		quotationMap.put(ConstantsUtil.QuotationMap.PERSONAL_DOC_TYPE,"R");
		quotationMap.put(ConstantsUtil.QuotationMap.PARTICIPANT_PERSONAL_ID,"20100995523");
		quotationMap.put(ConstantsUtil.QuotationModMap.CONTACT_EMAIL_DESC,"hans.sanchaez@bbva.com");
		quotationMap.put(ConstantsUtil.QuotationModMap.CUSTOMER_PHONE_DESC,"977710002");
		quotationMap.put(ConstantsUtil.QuotationMap.FIELD_RFQ_INTERNAL_ID,null);
		quotationMap.put("INCOMES_PAYROLL_AMOUNT",97100);
		quotationMap.put("PAYROLL_CURRENCY_ID","PEN");
		quotationMap.put("PAYROLL_EMPLOYEE_NUMBER",2);
		quotationMap.put("EMPLOYEES_IND_TYPE","1");

		return quotationMap;
	}

	private Map<String,Object> dataPaymentByQuotationId() {
		Map<String,Object> quotationMap = new HashMap<>();

		quotationMap.put(ConstantsUtil.InsuranceContract.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE,"N");
		quotationMap.put("DOMICILE_CONTRACT_ID","4919108221879862");
		quotationMap.put(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_FREQUENCY_NAME,"ANUAL");
		quotationMap.put("INSURANCE_CONTRACT_ENTITY_ID","0011");
		quotationMap.put("INSURANCE_CONTRACT_BRANCH_ID","0284");
		quotationMap.put("INSRC_CONTRACT_INT_ACCOUNT_ID","4000998713");
		quotationMap.put("INSURED_AMOUNT",10000);
		quotationMap.put("CURRENCY_ID","PEN");

		return quotationMap;
	}

	private List<Map<String,Object>> dataParticipants(){
		List<Map<String,Object>> list = new ArrayList<>();

		Map<String,Object> participant1 = new HashMap<>();
		participant1.put("PARTICIPANT_ROLE_ID",new BigDecimal("1"));
		participant1.put("PARTY_ORDER_NUMBER","1");
		participant1.put("PERSONAL_DOC_TYPE","R");
		participant1.put("PARTICIPANT_PERSONAL_ID","20788471883");
		participant1.put("CUSTOMER_ID","00051578");

		list.add(participant1);

		Map<String,Object> participant2 = new HashMap<>();
		participant2.put("PARTICIPANT_ROLE_ID",new BigDecimal("3"));
		participant2.put("PARTY_ORDER_NUMBER","1");
		participant2.put("PERSONAL_DOC_TYPE","L");
		participant2.put("PARTICIPANT_PERSONAL_ID","72638803");
		participant2.put("CUSTOMER_ID","97170064");

		list.add(participant2);

		return list;
	}


	/**
	 * CASO 1.1: FLUJO COMPLETO DE COTIZACIÓN RÁPIDA SIN DATOS DE CONTRATACIÓN DEVUELVE TODOS LOS DATOS POSIBLES CORRECTOS
	 */

	@Test
	public void executeTestFastQuotationWithoutContractingOK() {

		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID", policyQuotaInternalId)).thenReturn(Collections.emptyMap());
		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONREFERENCE", policyQuotaInternalId)).thenReturn(Collections.emptyMap());

		responseRimac.getPayload().getPlan().setFechaInicio("2024-05-20");
		responseRimac.getPayload().getPlan().setFechaFin("2025-05-20");
		responseRimac.getPayload().getPlan().getCoberturas().get(0).setPrincipal("S");
		responseRimac.getPayload().setDatosParticulares(Collections.emptyList());
		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getId());
		Assert.assertNotNull(response.getQuotationDate());
		Assert.assertNotNull(response.getEmployees());
		Assert.assertNotNull(response.getEmployees().getAreMajorityAge());
		Assert.assertNotNull(response.getEmployees().getEmployeesNumber());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount().getAmount());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount().getCurrency());
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getId());
		Assert.assertNotNull(response.getProduct().getName());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getIsSelected());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getTotalInstallment());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getInstallmentPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().get(0).getInstallmentPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages());
		Assert.assertEquals(6, response.getProduct().getPlans().get(0).getCoverages().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(2).getDescription());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getId());
		Assert.assertEquals("MAIN",response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType().getId());
		Assert.assertEquals("OBLIGATORIA",response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType().getName());
		Assert.assertEquals("INCLUDED",response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getId());
		Assert.assertEquals("INCLUIDA",response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getBenefits());
		Assert.assertNull(response.getProduct().getPlans().get(0).getExclusions());
		Assert.assertNotNull(response.getValidityPeriod());
		Assert.assertNotNull(response.getValidityPeriod().getStartDate());
		Assert.assertNotNull(response.getValidityPeriod().getEndDate());
		Assert.assertNotNull(response.getParticipants());
		Assert.assertNull(response.getPaymentMethod());
		Assert.assertNull(response.getBank());
		Assert.assertNull(response.getInsuredAmount());
	}


	/**
	 * CASO 1.2: FLUJO COMPLETO DE COTIZACIÓN RÁPIDA CON DATOS DE CONTRATACIÓN DEVUELVE TODOS LOS DATOS POSIBLES CORRECTOS
	 */

	@Test
	public void executeTestFastQuotationWithContractingOK() {

		Map<String,Object> paymentMap = dataPaymentByQuotationId();
		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID", policyQuotaInternalId)).thenReturn(paymentMap);

		Map<String,Object> argumentsParticipants = new HashMap<>();
		argumentsParticipants.put("INSURANCE_CONTRACT_ENTITY_ID","0011");
		argumentsParticipants.put("INSURANCE_CONTRACT_BRANCH_ID","0284");
		argumentsParticipants.put("INSRC_CONTRACT_INT_ACCOUNT_ID","4000998713");

		List<Map<String,Object>> participantsMap = dataParticipants();
		Mockito.when(pisdr402.executeGetListASingleRow("PISD.FIND_PARTICIPANTS_FROM_CONTRACT",argumentsParticipants)).thenReturn(participantsMap);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getId());
		Assert.assertNotNull(response.getQuotationDate());
		Assert.assertNotNull(response.getEmployees());
		Assert.assertNotNull(response.getEmployees().getAreMajorityAge());
		Assert.assertNotNull(response.getEmployees().getEmployeesNumber());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount().getAmount());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount().getCurrency());
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getId());
		Assert.assertNotNull(response.getProduct().getName());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getIsSelected());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getTotalInstallment());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getInstallmentPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().get(0).getInstallmentPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages());
		Assert.assertEquals(6, response.getProduct().getPlans().get(0).getCoverages().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(2).getDescription());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getBenefits());
		Assert.assertNull(response.getProduct().getPlans().get(0).getExclusions());
		Assert.assertNull(response.getValidityPeriod());
		Assert.assertNotNull(response.getParticipants());
		Assert.assertEquals(2,response.getParticipants().size());
		Assert.assertNotNull(response.getPaymentMethod());
		Assert.assertNotNull(response.getBank());
		Assert.assertNotNull(response.getInsuredAmount());
	}


	/*
	* CASO 2.1: FLUJO COTIZACIÓN NORMAL CON DATOS DE CONTRATACIÓN DE LA COTIZACIÓN DE REFERENCIA DEVUELVE TODOS LOS CAMPOS POSIBLES CORRECTOS
	 */
	@Test
	public void executeTestNormalQuotationWithContractingByQuotationReferenceOK() throws IOException{
		Map<String,Object> quotatioNDetailMap = dataQuotationDetailByQuotationId();
		quotatioNDetailMap.put("RFQ_INTERNAL_ID","01728424246800");
		quotatioNDetailMap.put("EMPLOYEES_IND_TYPE","");
		Mockito.when(pisdr402.executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId))
				.thenReturn(quotatioNDetailMap);

		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID", policyQuotaInternalId)).thenReturn(Collections.emptyMap());
		Map<String,Object> paymentMap = dataPaymentByQuotationId();
		paymentMap.put("DOMICILE_CONTRACT_ID","00110241000199015630");
		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONREFERENCE", policyQuotaInternalId)).thenReturn(paymentMap);

		responseRimac = mockData.getResponseNormalQuotationDetailRimac();

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(new ResponseEntity<>(responseRimac, HttpStatus.OK));

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getId());
		Assert.assertNotNull(response.getQuotationDate());
		Assert.assertNull(response.getEmployees());
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getId());
		Assert.assertNotNull(response.getProduct().getName());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getIsSelected());
		Assert.assertNull(response.getProduct().getPlans().get(0).getTotalInstallment());
		Assert.assertEquals(0, response.getProduct().getPlans().get(0).getInstallmentPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages());
		Assert.assertEquals(6, response.getProduct().getPlans().get(0).getCoverages().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getBenefits());
		Assert.assertEquals(3, response.getProduct().getPlans().get(0).getBenefits().size());
		Assert.assertNotNull(response.getPaymentMethod());
		Assert.assertNotNull(response.getBank());
		Assert.assertNotNull(response.getInsuredAmount());

		//Verifica que llama a las librerías externas
		Mockito.verify(pisdr401,Mockito.atLeastOnce()).executeGetProductById(
				ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC, policyQuotaInternalId);
		Mockito.verify(pisdr402,Mockito.atLeastOnce()).executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId);
		Mockito.verify(pisdr402,Mockito.atLeastOnce()).executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONREFERENCE", Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID", input.getQuotationId()));
		Mockito.verify(externalApiConnector,Mockito.atLeastOnce()).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap());
		Mockito.verify(pisdr402,Mockito.never()).executeInsertSingleRow(ConstantsUtil.QueriesName.QUERY_UPDATE_PREMIUM_AMOUNT_IN_NORMAL_QUOTATION,new HashMap<>());
	}


	/*
	 * CASO 2.2: FLUJO COTIZACIÓN NORMAL CON DATOS DE CONTRATACIÓN DE COTIZACIÓN NORMAL DEVUELVE TODOS LOS CAMPOS POSIBLES CORRECTOS
	 */
	@Test
	public void executeTestNormalQuotationWithContractingByNormalQuotationOK() throws IOException{
		Map<String,Object> quotatioNDetailMap = dataQuotationDetailByQuotationId();
		quotatioNDetailMap.put("RFQ_INTERNAL_ID","01728424246800");
		quotatioNDetailMap.put("EMPLOYEES_IND_TYPE","");
		Mockito.when(pisdr402.executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId))
				.thenReturn(quotatioNDetailMap);

		Map<String,Object> paymentMap = dataPaymentByQuotationId();
		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID", policyQuotaInternalId)).thenReturn(paymentMap);

		responseRimac = mockData.getResponseNormalQuotationDetailRimac();

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(new ResponseEntity<>(responseRimac, HttpStatus.OK));

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getId());
		Assert.assertNotNull(response.getQuotationDate());
		Assert.assertNull(response.getEmployees());
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getId());
		Assert.assertNotNull(response.getProduct().getName());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getIsSelected());
		Assert.assertNull(response.getProduct().getPlans().get(0).getTotalInstallment());
		Assert.assertEquals(0, response.getProduct().getPlans().get(0).getInstallmentPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages());
		Assert.assertEquals(6, response.getProduct().getPlans().get(0).getCoverages().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getBenefits());
		Assert.assertEquals(3, response.getProduct().getPlans().get(0).getBenefits().size());
		Assert.assertNotNull(response.getPaymentMethod());
		Assert.assertNotNull(response.getBank());
		Assert.assertNotNull(response.getInsuredAmount());

		//Verifica que llama a las librerías externas
		Mockito.verify(pisdr401,Mockito.atLeastOnce()).executeGetProductById(
				ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC, policyQuotaInternalId);
		Mockito.verify(pisdr402,Mockito.atLeastOnce()).executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId);
		Mockito.verify(pisdr402,Mockito.atLeastOnce()).executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID", Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID", input.getQuotationId()));
		Mockito.verify(externalApiConnector,Mockito.atLeastOnce()).exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap());
		Mockito.verify(pisdr402,Mockito.never()).executeInsertSingleRow(ConstantsUtil.QueriesName.QUERY_UPDATE_PREMIUM_AMOUNT_IN_NORMAL_QUOTATION,new HashMap<>());
	}

	/**
	 * CASO 3: FLUJO COTIZACIÓN NORMAL ACTUALIZANDO LA PRIMA BRUTA
	 */
	@Test
	public void executeTestUpdatePremiumAmount() throws IOException{
		Map<String,Object> quotatioNDetailMap = dataQuotationDetailByQuotationId();
		quotatioNDetailMap.put("RFQ_INTERNAL_ID","01728424246800");

		Mockito.when(pisdr402.executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId)).thenReturn(quotatioNDetailMap);

		responseRimac = mockData.getResponseNormalQuotationDetailRimac();
		responseRimac.getPayload().getPlan().setPrimaBruta(new BigDecimal("745.99"));

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(new ResponseEntity<>(responseRimac, HttpStatus.OK));

		Map<String,Object> arguments = new HashMap<>();
		arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID,input.getQuotationId());
		arguments.put(ConstantsUtil.QuotationModMap.INSURANCE_PRODUCT_ID,new BigDecimal("13"));
		arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE,"01");
		arguments.put(ConstantsUtil.QuotationModMap.PREMIUM_AMOUNT,new BigDecimal("745.99"));
		arguments.put(ConstantsUtil.QuotationMap.USER_AUDIT_ID,input.getTransactionCode());
		Mockito.when(pisdr402.executeInsertSingleRow(ConstantsUtil.QueriesName.QUERY_UPDATE_PREMIUM_AMOUNT_IN_NORMAL_QUOTATION,arguments)).thenReturn(1);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNotNull(response);

		//Se verifica que se llame al query de actualización de prima bruta

		Mockito.verify(pisdr402,Mockito.atLeastOnce()).executeInsertSingleRow(ConstantsUtil.QueriesName.QUERY_UPDATE_PREMIUM_AMOUNT_IN_NORMAL_QUOTATION,arguments);

	}

	//CASOS DE ERROR

	/**
	 * CASO 4: NO SE ENCONTRÓ DATOS DEL PRODUCTO
	 */

	@Test
	public void executeTestProductInformationNotFound() {
		mapProductInfo = null;
		Mockito.when(pisdr401.executeGetProductById(
						"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",
						policyQuotaInternalId))
				.thenReturn(mapProductInfo);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1, context.getAdviceList().size());
		Assert.assertEquals("RBVD01020092", context.getAdviceList().get(0).getCode());

	}


	/**
	 * CASO 5: LA COTIZACIÓN INGRESADA NO EXISTE
	 */

	@Test
	public void executeTestQuotationNotFound(){
		Mockito.when(pisdr402.executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID, policyQuotaInternalId))
				.thenReturn(null);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1,context.getAdviceList().size());
		Assert.assertEquals("RBVD01020095",context.getAdviceList().get(0).getCode());

		Mockito.verify(pisdr401,Mockito.atLeastOnce()).executeGetProductById(
				"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",
				Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID", quotationId));

	}


	/**
	 * CASO 6: ERROR AL LLAMAR AL API DE RIMAC
	 */

	@Test
	public void executeTestRimacResponseNull() {
		responseRimac = null;
		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.BAD_REQUEST)
		);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(input);

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1, context.getAdviceList().size());
		Assert.assertEquals("RBVD00000174", context.getAdviceList().get(0).getCode());
	}
	
}
