package com.bbva.rbvd.lib.r407.impl.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r402.PISDR402;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.PaymentDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.QuotationInputDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.service.dao.IQuotationDAO;
import com.bbva.rbvd.lib.r407.impl.transform.bean.PaymentBean;
import com.bbva.rbvd.lib.r407.impl.transform.bean.QuotationBean;
import com.bbva.rbvd.lib.r407.impl.transform.map.QuotationMap;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QuotationDAOImpl implements IQuotationDAO {

    private final PISDR402 pisdR402;

    public QuotationDAOImpl(PISDR402 pisdR402) {
        this.pisdR402 = pisdR402;
    }

    @Override
    public QuotationDAO getQuotationDetailByQuotationId(String quotationId) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID,quotationId);

        Map<String,Object> responseMap = this.pisdR402.executeGetASingleRow(ConstantsUtil.QueriesName.QUERY_FIND_QUOTATION_DETAIL_BY_QUOTATIONID,arguments);

        if(ValidateUtils.mapIsNullOrEmpty(responseMap)){
            throw new BusinessException("RBVD01020095",false,"No se encontraron datos de la cotización");
        }

        return QuotationBean.transformQuotationMapToBean(responseMap);
    }

    @Override
    public PaymentDAO getPaymentDetailsByQuotationId(String quotation) {
        Map<String,Object> arguments = new HashMap<>();
        PaymentDAO paymentDAO = null;

        arguments.put(ConstantsUtil.QuotationMap.POLICY_QUOTA_INTERNAL_ID,quotation);
        Map<String, Object> responsePaymentMap = this.pisdR402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONID",arguments);

        if(ValidateUtils.mapIsNullOrEmpty(responsePaymentMap)){
            Map<String,Object> responseQuotationRef = this.pisdR402.executeGetASingleRow("PISD.FIND_PAYMENTDATA_FROM_QUOTATIONREFERENCE",arguments);

            if(!ValidateUtils.mapIsNullOrEmpty(responseQuotationRef)){
                paymentDAO = PaymentBean.mapResponsePaymentToBean(responseQuotationRef);
            }
        }else{
            paymentDAO = PaymentBean.mapResponsePaymentToBean(responsePaymentMap);
        }

        return paymentDAO;
    }

    @Override
    public int updatePremiumAmount(QuotationInputDTO input, BigDecimal insuranceProductId, String modalityType, BigDecimal amount) {
        Map<String,Object> arguments = QuotationMap.argumentUpdatePremiumAmount(input, insuranceProductId, modalityType, amount);
        return this.pisdR402.executeInsertSingleRow(ConstantsUtil.QueriesName.QUERY_UPDATE_PREMIUM_AMOUNT_IN_NORMAL_QUOTATION,arguments);
    }


}
