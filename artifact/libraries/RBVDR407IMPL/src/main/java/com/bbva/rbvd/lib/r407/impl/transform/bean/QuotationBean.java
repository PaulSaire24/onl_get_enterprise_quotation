package com.bbva.rbvd.lib.r407.impl.transform.bean;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;

import java.util.Map;

public class QuotationBean {

    private QuotationBean(){}

    public static QuotationDAO transformQuotationMapToBean(Map<String,Object> map){
        QuotationDAO quotationDAO = new QuotationDAO();

        quotationDAO.setQuoteDate((String) map.get(ConstantsUtil.QuotationMap.QUOTE_DATE));
        quotationDAO.setInsuranceModalityType((String) map.get(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_TYPE));
        quotationDAO.setInsuranceProductType((String) map.get(ConstantsUtil.InsuranceProduct.FIELD_INSURANCE_PRODUCT_TYPE));
        quotationDAO.setInsuranceModalityName((String) map.get(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_MODALITY_NAME));
        quotationDAO.setInsurModalityDesc((String) map.get(ConstantsUtil.InsurancePrdModality.FIELD_INSUR_MODALITY_DESC));
        quotationDAO.setInsuranceCompanyModalityId((String) map.get(ConstantsUtil.InsurancePrdModality.FIELD_INSURANCE_COMPANY_MODALITY_ID));
        quotationDAO.setUserAuditId((String) map.get(ConstantsUtil.QuotationMap.USER_AUDIT_ID));
        quotationDAO.setCustomerId((String) map.get(ConstantsUtil.QuotationMap.CUSTOMER_ID));
        quotationDAO.setPolicyQuotaStatusType((String) map.get(ConstantsUtil.QuotationMap.FIELD_POLICY_QUOTA_STATUS_TYPE));
        quotationDAO.setPersonalDocType((String) map.get(ConstantsUtil.QuotationMap.PERSONAL_DOC_TYPE));
        quotationDAO.setParticipantPersonalId((String) map.get(ConstantsUtil.QuotationMap.PARTICIPANT_PERSONAL_ID));
        quotationDAO.setContactEmailDesc((String) map.get(ConstantsUtil.QuotationModMap.CONTACT_EMAIL_DESC));
        quotationDAO.setCustomerPhoneDesc((String) map.get(ConstantsUtil.QuotationModMap.CUSTOMER_PHONE_DESC));
        quotationDAO.setRfqInternalId((String) map.get(ConstantsUtil.QuotationMap.FIELD_RFQ_INTERNAL_ID));
        quotationDAO.setPayrollAmount(ConvertUtils.getBigDecimalValue(map.get(ConstantsUtil.InsuranceQuoteCoLife.FIELD_INCOMES_PAYROLL_AMOUNT)));
        quotationDAO.setPayrollCurrencyId((String) map.get(ConstantsUtil.InsuranceQuoteCoLife.PAYROLL_CURRENCY_ID));
        quotationDAO.setEmployeeNumber(ConvertUtils.getBigDecimalValue(map.get(ConstantsUtil.InsuranceQuoteCoLife.FIELD_PAYROLL_EMPLOYEE_NUMBER)));
        quotationDAO.setEmployeesIndType((String) map.get(ConstantsUtil.InsuranceQuoteCoLife.EMPLOYEES_IND_TYPE));

        return  quotationDAO;
    }

}
