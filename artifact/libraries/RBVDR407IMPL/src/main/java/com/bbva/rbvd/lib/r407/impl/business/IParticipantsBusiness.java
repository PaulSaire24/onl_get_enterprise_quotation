package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;

import java.util.List;

public interface IParticipantsBusiness {

    List<ParticipantDTO> constructParticipantsInfo(QuotationEntity quotationEntity);

}
