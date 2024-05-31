package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.IdentityDocumentDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.InsrncParticipantDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IParticipantsBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;

public class PariticipantsBusinessImpl implements IParticipantsBusiness {


    private final ApplicationConfigurationService applicationConfigurationService;

    public PariticipantsBusinessImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public List<ParticipantDTO> constructParticipants(QuotationDAO quotationDAO,List<InsrncParticipantDAO> participantsFromDB) {
        List<ParticipantDTO> participantDTOS = new ArrayList<>();
        BigDecimal roleIdLegalRepresentative = new BigDecimal("3");

        Optional.ofNullable(quotationDAO)
                .filter(q -> ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(q.getCustomerId(), q.getPersonalDocType(), q.getParticipantPersonalId())))
                .map(this::createParticipantHolder)
                .ifPresent(participantDTOS::add);

        Optional.ofNullable(participantsFromDB)
                .orElse(Collections.emptyList())
                .stream()
                .filter(participant -> roleIdLegalRepresentative.equals(participant.getParticipantRoleId()))
                .map(this::createParticipantLegalRepresentative)
                .forEach(participantDTOS::add);

        return participantDTOS;
    }

    private ParticipantDTO createParticipantHolder(QuotationDAO quotationDAO) {
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

        return participantHolder;
    }

    private ParticipantDTO createParticipantLegalRepresentative(InsrncParticipantDAO participants) {
        ParticipantDTO participantLegalRepresentative = new ParticipantDTO();

        participantLegalRepresentative.setId(participants.getCustomerId());

        String documentType = participants.getPersonalDocType();
        String documentNumber = participants.getParticipantPersonalId();

        if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(documentType,documentNumber))){
            participantLegalRepresentative.setIdentityDocument(getIdentityDocumentFromDB(documentType,documentNumber));
        }

        DescriptionDTO participantType = new DescriptionDTO();
        participantType.setId("LEGAL_REPRESENTATIVE");
        participantLegalRepresentative.setParticipantType(participantType);

        return participantLegalRepresentative;
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
