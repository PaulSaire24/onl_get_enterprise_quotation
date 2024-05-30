package com.bbva.rbvd.lib.r407.impl.transform.bean;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;

import java.util.Map;

public class PaymentBean {

    private PaymentBean(){}

    public static PaymentDAO mapResponsePaymentToBean(Map<String,Object> responsePaymentMap){
        PaymentDAO paymentDAO = new PaymentDAO();

        paymentDAO.setEntity((String) responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_INSURANCE_CONTRACT_ENTITY_ID));
        paymentDAO.setBranch((String) responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_INSURANCE_CONTRACT_BRANCH_ID));
        paymentDAO.setCurrency((String) responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_CURRENCY_ID));
        paymentDAO.setAutomaticDebitIndicatorType((String) responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE));
        paymentDAO.setDomicileContractId((String) responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_DOMICILE_CONTRACT_ID));
        paymentDAO.setPaymentFrequencyName((String) responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_PAYMENT_FREQUENCY_NAME));
        paymentDAO.setInsuredAmount(ConvertUtils.getBigDecimalValue(responsePaymentMap.get(ConstantsUtil.InsuranceContract.FIELD_INSURED_AMOUNT)));
        paymentDAO.setAccountId((String) responsePaymentMap.get("INSRC_CONTRACT_INT_ACCOUNT_ID"));

        return paymentDAO;
    }

}
