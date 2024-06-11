package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;

public class PaymentBusinessImplTest {

    private PaymentBusinessImpl paymentBusinessImpl;

    @Before
    public void setUp() throws IOException {
        ApplicationConfigurationService applicationConfigurationService = Mockito.mock(ApplicationConfigurationService.class);
        paymentBusinessImpl = new PaymentBusinessImpl(applicationConfigurationService);

        Mockito.when(applicationConfigurationService.getProperty("Mensual")).thenReturn("MONTHLY");
        Mockito.when(applicationConfigurationService.getProperty("Anual")).thenReturn("ANNUAL");

    }

    /**
     * CASO 1: RETORNA EL MÉTODO DE PAGO COMPLETO CON DATOS DE CUENTA
     */
    @Test
    public void executeTestPaymentMethodWithAccount(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("00110893476222000008");
        paymentDAO.setInsuredAmount(new BigDecimal("10000"));
        paymentDAO.setBranch("0446");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        PaymentMethodDTO paymentMethodDTO = paymentBusinessImpl.constructPaymentMethod(paymentDAO);

        Assert.assertNotNull(paymentMethodDTO);
        Assert.assertEquals(ConstantsUtil.PaymentMethod.METHOD_TYPE_SAVINGS_ACCOUNT, paymentMethodDTO.getPaymentType());
        Assert.assertEquals("ANNUAL", paymentMethodDTO.getInstallmentFrequency());
        Assert.assertEquals(1, paymentMethodDTO.getRelatedContracts().size());
        Assert.assertEquals(paymentDAO.getDomicileContractId(), paymentMethodDTO.getRelatedContracts().get(0).getContractId());
        Assert.assertEquals("ACCOUNT", paymentMethodDTO.getRelatedContracts().get(0).getProduct().getId());
    }


    /**
     * CASO 2: RETORNA EL MÉTODO DE PAGO COMPLETO CON DATOS DE TARJETA
     */
    @Test
    public void executeTestPaymentMethodWithCard(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("4919108221879862");
        paymentDAO.setInsuredAmount(new BigDecimal("10000"));
        paymentDAO.setBranch("0281");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        PaymentMethodDTO paymentMethodDTO = paymentBusinessImpl.constructPaymentMethod(paymentDAO);

        Assert.assertNotNull(paymentMethodDTO);
        Assert.assertEquals(ConstantsUtil.PaymentMethod.METHOD_TYPE_CREDIT_CARD, paymentMethodDTO.getPaymentType());
        Assert.assertEquals("ANNUAL", paymentMethodDTO.getInstallmentFrequency());
        Assert.assertEquals(1, paymentMethodDTO.getRelatedContracts().size());
        Assert.assertEquals(paymentDAO.getDomicileContractId(), paymentMethodDTO.getRelatedContracts().get(0).getContractId());
        Assert.assertEquals("CARD", paymentMethodDTO.getRelatedContracts().get(0).getProduct().getId());
    }


    /**
     * CASO 3: RETORNA MÉTODO DE PAGO COMPLETO CON UN TIP ODE PRODUCTO DIFERENTE A CUENTA O TARJETA
     */
    @Test
    public void executeTestPaymentMethodWithOtherProduct(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("S");
        paymentDAO.setPaymentFrequencyName("MENSUAL");
        paymentDAO.setDomicileContractId("49191082218793340987");
        paymentDAO.setInsuredAmount(new BigDecimal("10000"));
        paymentDAO.setBranch("0281");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        PaymentMethodDTO paymentMethodDTO = paymentBusinessImpl.constructPaymentMethod(paymentDAO);

        Assert.assertNotNull(paymentMethodDTO);
        Assert.assertEquals(ConstantsUtil.PaymentMethod.METHOD_TYPE_DIRECT_DEBIT, paymentMethodDTO.getPaymentType());
        Assert.assertEquals("MONTHLY", paymentMethodDTO.getInstallmentFrequency());
        Assert.assertEquals(1, paymentMethodDTO.getRelatedContracts().size());
        Assert.assertEquals(paymentDAO.getDomicileContractId(), paymentMethodDTO.getRelatedContracts().get(0).getContractId());
        Assert.assertEquals("OTHER", paymentMethodDTO.getRelatedContracts().get(0).getProduct().getId());
    }


    /**
     * CASO 4: RETORNA MÉTODO DE PAGO VACÍO PORQUE NO SE ENVÍA DATOS DE LA CUENTA O TARJETA
     */
    @Test
    public void executeTestPaymentDataEmpty(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("");
        paymentDAO.setInsuredAmount(new BigDecimal("10000"));
        paymentDAO.setBranch("0281");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        PaymentMethodDTO paymentMethodDTO = paymentBusinessImpl.constructPaymentMethod(paymentDAO);

        Assert.assertNull(paymentMethodDTO);
    }


    /**
     * CASO 5: RETORNA DATOS DE BANCO Y OFICINA COMPLETOS
     */
    @Test
    public void executeTestBankDataCompleted(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("00110241770199015630");
        paymentDAO.setInsuredAmount(new BigDecimal("10000"));
        paymentDAO.setBranch("0281");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        BankDTO bank = paymentBusinessImpl.constructBank(paymentDAO.getEntity(),paymentDAO.getBranch());

        Assert.assertNotNull(bank);
        Assert.assertNotNull(bank.getId());
        Assert.assertNotNull(bank.getBranch());
        Assert.assertNotNull(bank.getBranch().getId());
    }


    /**
     * CASO 6: RETORNA DATOS DE BANCO Y OFICINA VACIOS
     */
    @Test
    public void executeTestBankDataNull(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("00110241770199015630");
        paymentDAO.setInsuredAmount(new BigDecimal("10000"));
        paymentDAO.setBranch("");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        BankDTO bank = paymentBusinessImpl.constructBank(paymentDAO.getEntity(),paymentDAO.getBranch());

        Assert.assertNull(bank);
    }


    /**
     * CASO 7: RETORNA DATOS DE SUMA ASEGURADA COMPLETOS
     */
    @Test
    public void executeTestInsuredAmountCompleted(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("00110241770199015630");
        paymentDAO.setInsuredAmount(new BigDecimal("15000"));
        paymentDAO.setBranch("0732");
        paymentDAO.setCurrency("PEN");
        paymentDAO.setEntity("0011");

        AmountDTO insuredAmount = paymentBusinessImpl.constructInsuredAmount(new ResponsePayloadQuotationDetailBO(),paymentDAO);

        Assert.assertNotNull(insuredAmount);
        Assert.assertNotNull(insuredAmount.getAmount());
        Assert.assertNotNull(insuredAmount.getCurrency());
    }


    /**
     * CASO 8: RETORNA DATOS DE SUMA ASEGURADA VACIOS
     */
    @Test
    public void executeTestInsuredAmountNull(){
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setAutomaticDebitIndicatorType("N");
        paymentDAO.setPaymentFrequencyName("ANUAL");
        paymentDAO.setDomicileContractId("00110241770199015630");
        paymentDAO.setInsuredAmount(null);
        paymentDAO.setBranch("0732");
        paymentDAO.setCurrency("");
        paymentDAO.setEntity("0011");

        AmountDTO insuredAmount = paymentBusinessImpl.constructInsuredAmount(new ResponsePayloadQuotationDetailBO(),paymentDAO);

        Assert.assertNull(insuredAmount);
    }


}