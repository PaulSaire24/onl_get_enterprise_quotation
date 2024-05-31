package com.bbva.rbvd.lib.r407.impl.service.dao.impl;

import com.bbva.pisd.lib.r402.PISDR402;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.InsrncParticipantDAO;
import com.bbva.rbvd.lib.r407.impl.service.dao.IParticipantsDAO;
import com.bbva.rbvd.lib.r407.impl.transform.bean.ParticipantsBean;
import java.util.Collections;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParticipantsDAOImpl implements IParticipantsDAO {

    private final PISDR402 pisdr402;

    public ParticipantsDAOImpl(PISDR402 pisdr402) {
        this.pisdr402 = pisdr402;
    }

    @Override
    public List<InsrncParticipantDAO> getParticipantsByContract(String entity, String branch, String accountId) {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("INSURANCE_CONTRACT_ENTITY_ID", entity);
        arguments.put("INSURANCE_CONTRACT_BRANCH_ID", branch);
        arguments.put("INSRC_CONTRACT_INT_ACCOUNT_ID", accountId);

        List<Map<String,Object>> listParticipants = this.pisdr402.executeGetListASingleRow("PISD.FIND_PARTICIPANTS_FROM_CONTRACT",arguments);

        if(!CollectionUtils.isEmpty(listParticipants)){
            return listParticipants.stream().map(ParticipantsBean::mapResponseToBean).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
