package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.pisd.dto.insurancedao.entities.QuotationModEntity;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ContactDetailsDTO;

import java.util.List;

public interface IContactDetailsBusiness {

    List<ContactDetailsDTO> constructContactDetailsInfo(QuotationModEntity quotationModEntity);

}
