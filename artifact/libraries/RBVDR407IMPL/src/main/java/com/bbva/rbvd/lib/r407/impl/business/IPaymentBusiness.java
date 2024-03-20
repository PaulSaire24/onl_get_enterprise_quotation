package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;


public interface IPaymentBusiness {

    PaymentMethodDTO constructPaymentMethodInfo();

    BankDTO constructBankInfo();

}
