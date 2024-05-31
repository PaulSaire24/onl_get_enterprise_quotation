package com.bbva.rbvd.lib.r407.impl.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.InsrncParticipantDAO;

import java.util.List;

public interface IParticipantsDAO {

    List<InsrncParticipantDAO> getParticipantsByContract(String entity, String branch, String accountId);

}
