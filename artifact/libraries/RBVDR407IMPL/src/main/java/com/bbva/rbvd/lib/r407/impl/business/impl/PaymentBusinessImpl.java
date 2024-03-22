package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.RelatedContractsDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IPaymentBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentBusinessImpl implements IPaymentBusiness {

    private final Map<String,Object> paymentDetailsMap;
    private final ApplicationConfigurationService applicationConfigurationService;


    public PaymentBusinessImpl(Map<String, Object> paymentDetailsMap,
                               ApplicationConfigurationService applicationConfigurationService) {
        this.paymentDetailsMap = paymentDetailsMap;
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public PaymentMethodDTO constructPaymentMethod() {
        String paymentType = (String) paymentDetailsMap.get(
                ConstantsUtil.InsuranceContract.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE);
        String frequency = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_FREQUENCY_NAME);

        if(ValidateUtils.allValuesNotNullOrEmpty(paymentType,frequency)){
            PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();

            paymentMethodDTO.setPaymentType(ConstantsUtil.StringConstants.S.equalsIgnoreCase(paymentType)
                    ? ConstantsUtil.PaymentMethod.METHOD_TYPE_DIRECT_DEBIT
                    : ConstantsUtil.PaymentMethod.METHOD_TYPE_CREDIT_CARD);
            paymentMethodDTO.setInstallmentFrequency(this.applicationConfigurationService.getProperty(
                    ConvertUtils.convertStringToUpperAndLowerCase(frequency)));
            paymentMethodDTO.setRelatedContracts(constructRelatedContracts(paymentDetailsMap));

            return paymentMethodDTO;
        }
        return null;
    }

    private static List<RelatedContractsDTO> constructRelatedContracts(Map<String,Object> paymentDetailsMap){
        String contractId = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_DOMICILE_CONTRACT_ID);
        String paymentMethodType = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_METHOD_TYPE);

        if(ValidateUtils.allValuesNotNullOrEmpty(contractId,paymentMethodType)){
            List<RelatedContractsDTO> relatedContractList = new ArrayList<>();
            RelatedContractsDTO relatedContractsDTO = new RelatedContractsDTO();

            relatedContractsDTO.setContractId(contractId);
            relatedContractsDTO.setNumber(contractId);
            DescriptionDTO productDTO = new DescriptionDTO();
            productDTO.setId("T".equalsIgnoreCase(paymentMethodType)
                    ? ConstantsUtil.PaymentMethod.PRODUCT_ID_CARD : ConstantsUtil.PaymentMethod.PRODUCT_ID_ACCOUNT);
            relatedContractsDTO.setProduct(productDTO);

            relatedContractList.add(relatedContractsDTO);

            return relatedContractList;
        }

        return null;
    }

    @Override
    public BankDTO constructBank() {
        String entity = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_INSURANCE_CONTRACT_ENTITY_ID);
        String branch = (String) paymentDetailsMap.get(ConstantsUtil.InsuranceContract.FIELD_CONTRACT_MANAGER_BRANCH_ID);

        if(ValidateUtils.allValuesNotNullOrEmpty(entity,branch)){
            BankDTO bankDTO = new BankDTO();
            bankDTO.setId(entity);

            DescriptionDTO branchDTO = new DescriptionDTO();
            branchDTO.setId(branch);
            bankDTO.setBranch(branchDTO);

            return bankDTO;
        }

        return null;
    }
}
