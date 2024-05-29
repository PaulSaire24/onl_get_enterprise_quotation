package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.IdentityDocumentDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IParticipantsBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PariticipantsBusinessImpl implements IParticipantsBusiness {

    private final ApplicationConfigurationService applicationConfigurationService;

    public PariticipantsBusinessImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public List<ParticipantDTO> constructParticipants(QuotationDAO quotationDAO) {
        List<ParticipantDTO> participantDTOS = new ArrayList<>();

        if(!ValidateUtils.stringIsNullOrEmpty(quotationDAO.getCustomerId())){
            ParticipantDTO participantHolder = new ParticipantDTO();

            participantHolder.setId(quotationDAO.getCustomerId());

            String documentType = quotationDAO.getPersonalDocType();
            String documentNumber = quotationDAO.getParticipantPersonalId();

            if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(documentType,documentNumber))){
                participantHolder.setIdentityDocument(getIdentityDocumentFromDB(documentType,documentNumber));
            }

            DescriptionDTO participantType = new DescriptionDTO();
            participantType.setId(ConstantsUtil.StringConstants.PARTICIPANT_TYPE_HOLDER);
            participantHolder.setParticipantType(participantType);

            participantDTOS.add(participantHolder);
        }

        return participantDTOS;
    }

    private IdentityDocumentDTO getIdentityDocumentFromDB(String documentType,String documentNumber) {
        IdentityDocumentDTO identityDocument = new IdentityDocumentDTO();

        DescriptionDTO documentTypeDTO = new DescriptionDTO();
        documentTypeDTO.setId(this.applicationConfigurationService.getProperty(documentType));
        identityDocument.setDocumentNumber(documentNumber);
        identityDocument.setDocumentType(documentTypeDTO);

        return identityDocument;
    }

}
