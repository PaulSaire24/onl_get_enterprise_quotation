package com.bbva.rbvd.lib.r407;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import javax.annotation.Resource;

import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.test.osgi.DummyBundleContext;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.*;
import com.bbva.rbvd.dto.enterpriseinsurance.createquotation.dto.CreateQuotationDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.GetQuotationDTO;
import com.bbva.rbvd.lib.r407.impl.RBVDR407Impl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RBVDR407Test {

	@Spy
	private Context context;
	@InjectMocks
	private RBVDR407Impl rbvdR407Impl ;
	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private RBVDR407 rbvdR407;
	private GetQuotationDTO requestInput;

	@Before
	public void setUp() throws Exception {
		context = new Context();
		ThreadContext.set(context);
		requestInput=new GetQuotationDTO();
	}
	private GetQuotationDTO createInput(){
		GetQuotationDTO input = new GetQuotationDTO();
		ProductDTO product = new ProductDTO();
		List<ContactDetailsDTO> contactDetails = new ArrayList<>();
		List<ParticipantDTO> participantes = new ArrayList<>();
		ContactDetailsDTO contacto1 = new ContactDetailsDTO();
		ContactDTO contacto = new ContactDTO();
		ParticipantDTO participnt1 = new ParticipantDTO();
		DescriptionDTO participantType = new DescriptionDTO();
		EmployeesDTO employees = new EmployeesDTO();
		DescriptionDTO busunessAgent = new DescriptionDTO();

		participnt1.setId("P041360");
		IdentityDocumentDTO document = new IdentityDocumentDTO();
		DescriptionDTO documentType = new DescriptionDTO();
		document.setDocumentNumber("73186739");
		documentType.setId("DNI");
		documentType.setDescription("DNI");
		document.setDocumentType(documentType);
		participnt1.setIdentityDocument(document);
		participantType.setId("123456");
		participantType.setName("Contract");
		participnt1.setParticipantType(participantType);
		participantes.add(participnt1);
		busunessAgent.setId("P021322");
		employees.setAreMajorityAge(true);
		employees.setEmployeesNumber(Long.valueOf(30));
		AmountDTO monthlyPayrollAmount = new AmountDTO();
		monthlyPayrollAmount.setCurrency("PEN");
		monthlyPayrollAmount.setAmount(BigDecimal.valueOf(20.00));
		employees.setMonthlyPayrollAmount((monthlyPayrollAmount));
		product.setId("503");
		contacto.setContactDetailType("EMAIL");
		contacto.setAddress("marco.yovera@bbva.com");
		contacto1.setContact(contacto);
		contactDetails.add(contacto1);

		input.setProduct(product);
		input.setParticipantDTO(participantes);
		input.setQuotationReference("2312313");
		input.setEmployees(employees);
		input.setBusinessAgent(busunessAgent);
		input.setContactDetailsDTO(contactDetails);


		return input;
	}
	@Test
	public void executeTest(){
		this.requestInput =createInput();
		rbvdR407Impl.executeGetQuotation("0011123412341");
	}


	
}
