package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.RelatedContractsDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IPaymentBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaymentBusinessImpl implements IPaymentBusiness {

    private final ApplicationConfigurationService applicationConfigurationService;

    public PaymentBusinessImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public PaymentMethodDTO constructPaymentMethod(PaymentDAO paymentDetails) {
        String debitIndicatorType = paymentDetails.getAutomaticDebitIndicatorType();
        String frequency = paymentDetails.getPaymentFrequencyName();
        String domicileContractId = paymentDetails.getDomicileContractId();

        if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(debitIndicatorType,frequency,domicileContractId))){
            PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();

            String type = getPaymentTypeByContractId(domicileContractId);
            String paymentMethod = determinePaymentMethod(type, debitIndicatorType);
            paymentMethodDTO.setPaymentType(paymentMethod);
            paymentMethodDTO.setInstallmentFrequency(this.applicationConfigurationService.getProperty(ConvertUtils.convertStringToUpperAndLowerCase(frequency)));
            paymentMethodDTO.setRelatedContracts(constructRelatedContracts(paymentDetails));

            return paymentMethodDTO;
        }
        return null;
    }

    private String determinePaymentMethod(String type, String debitIndicatorType) {
        if (type.equalsIgnoreCase(ConstantsUtil.PaymentMethod.PRODUCT_ID_CARD)) {
            return ConstantsUtil.PaymentMethod.METHOD_TYPE_CREDIT_CARD;
        } else if (ConstantsUtil.StringConstants.S.equalsIgnoreCase(debitIndicatorType)) {
            return ConstantsUtil.PaymentMethod.METHOD_TYPE_DIRECT_DEBIT;
        } else {
            return ConstantsUtil.PaymentMethod.METHOD_TYPE_SAVINGS_ACCOUNT;
        }
    }

    private static String getPaymentTypeByContractId(String contractId){
        if(contractId.length() <= 18 && (contractId.startsWith(ConstantsUtil.StringConstants.CARD_PREFIX_4)
                || contractId.startsWith(ConstantsUtil.StringConstants.CARD_PREFIX_5))){
            return ConstantsUtil.PaymentMethod.PRODUCT_ID_CARD;
        }else if(contractId.length() == 20 && contractId.startsWith(ConstantsUtil.StringConstants.ACCOUNT_PREFIX)){
            return ConstantsUtil.PaymentMethod.PRODUCT_ID_ACCOUNT;
        }else{
            return ConstantsUtil.StringConstants.OTHER_PRODUCT_ID;
        }
    }

    private static List<RelatedContractsDTO> constructRelatedContracts(PaymentDAO paymentDetails){
        String contractId = paymentDetails.getDomicileContractId();
        String productId = getPaymentTypeByContractId(contractId);

        RelatedContractsDTO relatedContractsDTO = new RelatedContractsDTO();

        relatedContractsDTO.setContractId(contractId);
        relatedContractsDTO.setNumber(contractId);
        DescriptionDTO productDTO = new DescriptionDTO();
        productDTO.setId(productId);
        relatedContractsDTO.setProduct(productDTO);

        return Collections.singletonList(relatedContractsDTO);
    }

    @Override
    public BankDTO constructBank(String entity, String branch) {
        if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(entity,branch))){
            BankDTO bankDTO = new BankDTO();
            bankDTO.setId(entity);

            DescriptionDTO branchDTO = new DescriptionDTO();
            branchDTO.setId(branch);
            bankDTO.setBranch(branchDTO);

            return bankDTO;
        }

        return null;
    }

    @Override
    public AmountDTO constructInsuredAmount(BigDecimal amount, String currency) {
        if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(amount,currency))) {
            AmountDTO insuredAmount = new AmountDTO();
            insuredAmount.setAmount(amount.doubleValue());
            insuredAmount.setCurrency(currency);

            return insuredAmount;
        }
        return null;
    }
}
