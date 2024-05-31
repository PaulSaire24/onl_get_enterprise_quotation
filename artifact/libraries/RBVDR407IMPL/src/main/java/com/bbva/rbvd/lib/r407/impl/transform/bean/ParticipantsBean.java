package com.bbva.rbvd.lib.r407.impl.transform.bean;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.InsrncParticipantDAO;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;

import java.util.Map;

public class ParticipantsBean {

    private ParticipantsBean(){}

    public static InsrncParticipantDAO mapResponseToBean(Map<String,Object> map){
        InsrncParticipantDAO participantDAO = new InsrncParticipantDAO();

        participantDAO.setParticipantRoleId(ConvertUtils.getBigDecimalValue(map.get("PARTICIPANT_ROLE_ID")));
        participantDAO.setOrderNumber(ConvertUtils.getBigDecimalValue(map.get("PARTY_ORDER_NUMBER")));
        participantDAO.setPersonalDocType((String) map.get("PERSONAL_DOC_TYPE"));
        participantDAO.setParticipantPersonalId((String) map.get("PARTICIPANT_PERSONAL_ID"));
        participantDAO.setCustomerId((String) map.get("CUSTOMER_ID"));

        return participantDAO;
    }

}
