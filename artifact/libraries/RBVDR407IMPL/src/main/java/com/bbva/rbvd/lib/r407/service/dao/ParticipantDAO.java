package com.bbva.rbvd.lib.r407.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.lib.r407.utils.ContansUtils;

public class ParticipantDAO {

    public ParticipantDTO getParticipantBDInfo(){
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setFirstName(ContansUtils.mockInternalData.PARTICIPANT_FIRST_NAME);
        participantDTO.setLastName(ContansUtils.mockInternalData.PARTICIPANT_LAST_NAME);
        participantDTO.setSecondLastName(ContansUtils.mockInternalData.PARTICIPANT_SECOND_LAST_NAME);
        return participantDTO;
    }
}
