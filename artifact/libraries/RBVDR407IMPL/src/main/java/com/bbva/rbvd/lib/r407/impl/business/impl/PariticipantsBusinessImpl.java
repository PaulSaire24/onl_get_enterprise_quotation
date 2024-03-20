package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.IdentityDocumentDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IParticipantsBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

public class PariticipantsBusinessImpl implements IParticipantsBusiness {

    private final ApplicationConfigurationService applicationConfigurationService;

    public PariticipantsBusinessImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public List<ParticipantDTO> constructParticipantsInfo(QuotationEntity quotationEntity) {
        List<ParticipantDTO> participantDTOS = new ArrayList<>();

        if(!ValidateUtils.stringIsNullOrEmpty(quotationEntity.getCustomerId())){
            ParticipantDTO participantHolder = new ParticipantDTO();

            participantHolder.setId(quotationEntity.getCustomerId());

            if(ValidateUtils.allValuesNotNullOrEmpty(
                    quotationEntity.getPersonalDocType(),quotationEntity.getParticipantPersonalId())){
                participantHolder.setIdentityDocument(getIdentityDocumentFromDB(quotationEntity));
            }

            DescriptionDTO participantType = new DescriptionDTO();
            participantType.setId(ConstantsUtil.StringConstants.PARTICIPANT_TYPE_HOLDER);
            participantHolder.setParticipantType(participantType);

            participantDTOS.add(participantHolder);
        }

        return participantDTOS;
    }

    private IdentityDocumentDTO getIdentityDocumentFromDB(QuotationEntity quotationEntity) {
        IdentityDocumentDTO identityDocument = new IdentityDocumentDTO();

        DescriptionDTO documentType = new DescriptionDTO();
        documentType.setId(this.applicationConfigurationService.getProperty(quotationEntity.getPersonalDocType()));
        identityDocument.setDocumentNumber(quotationEntity.getParticipantPersonalId());
        identityDocument.setDocumentType(documentType);

        return identityDocument;
    }

}
