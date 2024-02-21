package com.bbva.rbvd.lib.r407.service.impl;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.lib.r407.service.dao.BusinessAgentDAO;
import com.bbva.rbvd.lib.r407.service.dao.ParticipantDAO;
import com.bbva.rbvd.lib.r407.service.dao.ProductDAO;


public class ConsumerInternalService {
    public DescriptionDTO getBusinessAgent (String id){
        DescriptionDTO businessAgent;
        BusinessAgentDAO businessAgentDAO = new BusinessAgentDAO();
         businessAgent = businessAgentDAO.MockBDResponseBA();
        businessAgent.setId(id);
        return businessAgent;
    }
    public ParticipantDTO getParticipantInformation (String id){
        ParticipantDTO participant ;
        ParticipantDAO participantDAO = new ParticipantDAO();
        participant = participantDAO.getParticipantBDInfo();
        participant.setId(id);
        return participant;
    }
    public ProductDTO getProduct (String id){
        ProductDTO product ;
        ProductDAO productDAO = new ProductDAO();

        product = productDAO.getProductInfo();
        return product;
    }

}
