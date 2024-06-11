package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;


public interface IPaymentBusiness {

    PaymentMethodDTO constructPaymentMethod(PaymentDAO paymentDetails);

    BankDTO constructBank(String entity, String branchId);
    AmountDTO constructInsuredAmount(ResponsePayloadQuotationDetailBO payload, PaymentDAO paymentDetails);

}
