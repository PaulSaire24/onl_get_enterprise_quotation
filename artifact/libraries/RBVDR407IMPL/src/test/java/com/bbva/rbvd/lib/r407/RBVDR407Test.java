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
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponseQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.mock.MockData;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.RBVDErrors;
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

import java.io.IOException;

import java.util.*;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class RBVDR407Test {

	@Spy
	private Context context;

	private final RBVDR407Impl rbvdR407Impl = new RBVDR407Impl();

	private APIConnector externalApiConnector;
	private PISDR401 pisdr401;

	private PISDR402 pisdr402;
	private final String quotationId = "081400000381";
	private ResponseQuotationDetailBO responseRimac;
	private Map<String, Object> mapProductInfo = new HashMap<>();
	private Map<String,Object> policyQuotaInternalId;


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

		MockData mockData = MockData.getInstance();

		responseRimac = mockData.getResponseQuotationDetailRimac();

		mapProductInfo.put("INSURANCE_COMPANY_QUOTA_ID", "1cdd6ec3-67eb-443a-b4e6-ff10257cf205");
		mapProductInfo.put("PRODUCT_SHORT_DESC", "VIDALEY");
		mapProductInfo.put("INSURANCE_BUSINESS_NAME", "VIDA");
		mapProductInfo.put("INSURANCE_PRODUCT_ID", 13);
		Mockito.when(pisdr401.executeGetProductById(
						ConstantsUtil.QueriesName.QUERY_GET_COMPANY_QUOTA_ID_AND_PRODUCT_SHORT_DESC, policyQuotaInternalId))
				.thenReturn(mapProductInfo);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

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

		Mockito.when(pisdr014.executeSignatureConstruction(any(), any(), anyString(), any(), anyString()))
				.thenReturn(new SignatureAWS("authorization", "xAmzDate", "xApiKey", "traceId"));


		Map<String,Object> arguments = new HashMap<>();
		arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID, quotationId);
		arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_PRODUCT_ID,Mockito.anyString());
		arguments.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE, Mockito.anyString());
		Map<String,Object> employeeMap = dataEmployee();
		Mockito.when(pisdr402.executeGetASingleRow(
						ConstantsUtil.QueriesName.QUERY_FIND_ENTERPRISE_EMPLOYEE_FROM_QUOTATION, arguments))
				.thenReturn(employeeMap);

		Map<String,Object> paymentMap = dataPaymentByQuotationId();
		Mockito.when(pisdr402.executeGetASingleRow(
						ConstantsUtil.QueriesName.QUERY_FIND_PAYMENTMETHOD_FROM_QUOTATION, policyQuotaInternalId))
				.thenReturn(paymentMap);


		Map<String,Object> quotatioNDetailMap = dataQuotationDetailByQuotationId();
		Mockito.when(pisdr402.executeGetASingleRow(
				"PISD.FIND_QUOTATION_DETAIL_BY_INTERNAL_QUOTATION",
				policyQuotaInternalId)).thenReturn(quotatioNDetailMap);

	}

	private Map<String,Object> dataQuotationDetailByQuotationId() {
		Map<String,Object> quotationMap = new HashMap<>();

		quotationMap.put(ConstantsUtil.QuotationMap.QUOTE_DATE, "2024-03-19");
		quotationMap.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE, "02");
		quotationMap.put(ConstantsUtil.InsuranceProduct.FIELD_INSURANCE_PRODUCT_TYPE, "842");
		quotationMap.put("INSURANCE_MODALITY_NAME", "PLAN PLATA");
		quotationMap.put("INSUR_MODALITY_DESC", "PLAN 02 VIDA LEY");
		quotationMap.put(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_COMPANY_MODALITY_ID, "534272");
		quotationMap.put(ConstantsUtil.QuotationMap.USER_AUDIT_ID, "p121328");
		quotationMap.put(ConstantsUtil.QuotationMap.CUSTOMER_ID, "00692557");
		quotationMap.put(ConstantsUtil.QuotationMap.FIELD_POLICY_QUOTA_STATUS_TYPE,"COT");
		quotationMap.put(ConstantsUtil.QuotationMap.PERSONAL_DOC_TYPE,"R");
		quotationMap.put(ConstantsUtil.QuotationMap.PARTICIPANT_PERSONAL_ID,"20788661950");
		quotationMap.put(ConstantsUtil.QuotationModMap.CONTACT_EMAIL_DESC,"hans.sanchaez@bbva.com");
		quotationMap.put(ConstantsUtil.QuotationModMap.CUSTOMER_PHONE_DESC,"999999999");
		quotationMap.put(ConstantsUtil.QuotationMap.FIELD_RFQ_INTERNAL_ID,null);

		return quotationMap;
	}

	private Map<String,Object> dataEmployee() {
		Map<String,Object> quotationMap = new HashMap<>();

		quotationMap.put("INCOMES_PAYROLL_AMOUNT",8521.56);
		quotationMap.put("CURRENCY_ID","PEN");
		quotationMap.put("PAYROLL_EMPLOYEE_NUMBER",3);
		quotationMap.put("YEARS_OLD_18_65_EMPLOYEES_IND_TYPE","1");

		return quotationMap;
	}

	private Map<String,Object> dataPaymentByQuotationId() {
		Map<String,Object> quotationMap = new HashMap<>();

		quotationMap.put(ConstantsUtil.InsuranceContract.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE,"S");
		quotationMap.put("DOMICILE_CONTRACT_ID","5123128224957563");
		quotationMap.put(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_FREQUENCY_NAME,"MENSUAL");
		quotationMap.put("PAYMENT_METHOD_TYPE","T");
		quotationMap.put("INSURANCE_CONTRACT_ENTITY_ID","0011");
		quotationMap.put("CONTRACT_MANAGER_BRANCH_ID","0826");

		return quotationMap;
	}

	@Test
	public void executeGetQuotationLogic_TestOK() {

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNotNull(response);

		Assert.assertNotNull(response.getId());

		Assert.assertNotNull(response.getQuotationDate());

		Assert.assertNotNull(response.getEmployees());
		Assert.assertNotNull(response.getEmployees().getAreMajorityAge());
		Assert.assertNotNull(response.getEmployees().getEmployeesNumber());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount().getAmount());
		Assert.assertNotNull(response.getEmployees().getMonthlyPayrollAmount().getCurrency());

		Assert.assertEquals(true,response.getEmployees().getAreMajorityAge());
		Assert.assertEquals(3,response.getEmployees().getEmployeesNumber().intValue());
		Assert.assertEquals(new Double(8521.56),response.getEmployees().getMonthlyPayrollAmount().getAmount());
		Assert.assertEquals("PEN",response.getEmployees().getMonthlyPayrollAmount().getCurrency());

		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getId());
		Assert.assertNotNull(response.getProduct().getName());
		Assert.assertEquals(responseRimac.getPayload().getProducto(), response.getProduct().getName());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertEquals("PLAN PLATA",
				response.getProduct().getPlans().get(0).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getIsSelected());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getTotalInstallment());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getInstallmentPlans());
		Assert.assertEquals(1, response.getProduct().getPlans().get(0).getInstallmentPlans().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages());
		Assert.assertEquals(3, response.getProduct().getPlans().get(0).getCoverages().size());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(2).getDescription());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getId());
		Assert.assertEquals("MAIN",response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType().getId());
		Assert.assertEquals("OBLIGATORIA",response.getProduct().getPlans().get(0).getCoverages().get(0).getCoverageType().getName());
		Assert.assertEquals("MAIN",response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getId());
		Assert.assertEquals("OBLIGATORIA",response.getProduct().getPlans().get(0).getCoverages().get(1).getCoverageType().getName());
		Assert.assertEquals("MAIN",response.getProduct().getPlans().get(0).getCoverages().get(2).getCoverageType().getId());
		Assert.assertEquals("OBLIGATORIA",response.getProduct().getPlans().get(0).getCoverages().get(2).getCoverageType().getName());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getBenefits());
		Assert.assertNull(response.getProduct().getPlans().get(0).getExclusions());

		Assert.assertNotNull(response.getValidityPeriod());
		Assert.assertNotNull(response.getValidityPeriod().getStartDate());
		Assert.assertNotNull(response.getValidityPeriod().getEndDate());
		Assert.assertEquals(ConvertUtils.convertStringDateToDate(responseRimac.getPayload().getPlan().getFechaInicio()),
				response.getValidityPeriod().getStartDate());
		Assert.assertEquals(ConvertUtils.convertStringDateToDate(responseRimac.getPayload().getPlan().getFechaFin()),
				response.getValidityPeriod().getEndDate());

		Assert.assertNotNull(response.getParticipants());
		Assert.assertEquals(1,response.getParticipants().size());
		Assert.assertNotNull(response.getParticipants().get(0).getId());
		Assert.assertNotNull(response.getParticipants().get(0).getParticipantType().getId());
		Assert.assertNotNull(response.getParticipants().get(0).getIdentityDocument().getDocumentNumber());
		Assert.assertNotNull(response.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());

		Assert.assertNotNull(response.getPaymentMethod());
		Assert.assertNotNull(response.getPaymentMethod().getPaymentType());
		Assert.assertNotNull(response.getPaymentMethod().getInstallmentFrequency());
		Assert.assertEquals(1,response.getPaymentMethod().getRelatedContracts().size());

	}

	@Test
	public void executeTest_ProductInformationNotFound() {
		mapProductInfo = null;
		Mockito.when(pisdr401.executeGetProductById(
						"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",
						policyQuotaInternalId))
				.thenReturn(mapProductInfo);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1, context.getAdviceList().size());
		Assert.assertEquals(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getAdviceCode(), context.getAdviceList().get(0).getCode());
		Assert.assertEquals(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getMessage(),context.getAdviceList().get(0).getDescription());
	}

	@Test
	public void executeTest_QuotationNotFound(){
		Mockito.when(pisdr402.executeGetASingleRow("PISD.FIND_QUOTATION_DETAIL_BY_INTERNAL_QUOTATION",
						policyQuotaInternalId))
				.thenReturn(null);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId,"traceId");

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1,context.getAdviceList().size());
		Assert.assertEquals(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getAdviceCode(),context.getAdviceList().get(0).getCode());
		Assert.assertEquals(RBVDErrors.ERROR_PRODUCT_BY_SIMULATION.getMessage(),context.getAdviceList().get(0).getDescription());

		Mockito.verify(pisdr401,Mockito.atLeastOnce()).executeGetProductById(
				"PISD.GET_RIMAC_QUOT_AND_PRODUCT_INFO_BY_POLICY_QUOTA_INTERNAL_ID",
				Collections.singletonMap("POLICY_QUOTA_INTERNAL_ID", quotationId));

	}

	@Test
	public void executeTest_RimacResponseNull() {
		responseRimac = null;
		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.BAD_REQUEST)
		);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNull(response);
		Assert.assertNotNull(context.getAdviceList());
		Assert.assertEquals(1, context.getAdviceList().size());
		Assert.assertEquals(RBVDErrors.ERROR_CALL_QUOTATION_DETAIL_API.getAdviceCode(), context.getAdviceList().get(0).getCode());
		Assert.assertEquals(RBVDErrors.ERROR_CALL_QUOTATION_DETAIL_API.getMessage(),context.getAdviceList().get(0).getDescription());
	}

	@Test
	public void executeTest_WithoutPaymentData(){

		Mockito.when(pisdr402.executeGetASingleRow(
				ConstantsUtil.QueriesName.QUERY_FIND_PAYMENTMETHOD_FROM_QUOTATION,
						policyQuotaInternalId))
				.thenReturn(Collections.emptyMap());

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getId());
		Assert.assertNotNull(response.getQuotationDate());

		Assert.assertNull(response.getPaymentMethod());
		Assert.assertNull(response.getBank());
	}

	@Test
	public void executeTest_WithoutAuditData() {
		Map<String,Object> quotatioNDetailMap = dataQuotationDetailByQuotationId();
		quotatioNDetailMap.put(ConstantsUtil.QuotationMap.USER_AUDIT_ID, null);

		Mockito.when(pisdr402.executeGetASingleRow(
				"PISD.FIND_QUOTATION_DETAIL_BY_INTERNAL_QUOTATION",
						policyQuotaInternalId))
				.thenReturn(quotatioNDetailMap);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getQuotationDate());
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getValidityPeriod());
		Assert.assertNull(response.getBusinessAgent());
	}


	/*


	@Test
	public void executeGetQuotationLogic_WithBenefitsRimacIsNull() {
		responseRimac.getPayload().getPlan().setAsistencias(null);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertEquals(0, response.getProduct().getPlans().get(0).getBenefits().size());
		Assert.assertEquals(3, response.getProduct().getPlans().get(0).getCoverages().size());
	}

	@Test
	public void executeGetQuotationLogic_WithCoveragesRimacIsNull() {
		responseRimac.getPayload().getPlan().setCoberturas(null);

		Mockito.when(this.externalApiConnector.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				(Class<ResponseQuotationDetailBO>) Mockito.any(), Mockito.anyMap())).thenReturn(
				new ResponseEntity<>(responseRimac, HttpStatus.OK)
		);

		EnterpriseQuotationDTO response = rbvdR407Impl.executeGetQuotationLogic(quotationId, "traceId");

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getProduct());
		Assert.assertNotNull(response.getProduct().getPlans());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getId());
		Assert.assertNotNull(response.getProduct().getPlans().get(0).getName());
		Assert.assertEquals(3, response.getProduct().getPlans().get(0).getBenefits().size());
		Assert.assertEquals(0, response.getProduct().getPlans().get(0).getCoverages().size());
	}


	 */
	
}
