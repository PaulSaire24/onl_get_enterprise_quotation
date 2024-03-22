package com.bbva.rbvd.lib.r407.impl.business;


import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;

import java.util.List;

public interface IParticipantsBusiness {

    List<ParticipantDTO> constructParticipants(QuotationDAO quotationDAO);

}
