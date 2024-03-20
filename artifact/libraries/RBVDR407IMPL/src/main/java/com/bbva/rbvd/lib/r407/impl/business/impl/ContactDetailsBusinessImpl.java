package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.pisd.dto.insurancedao.entities.QuotationModEntity;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ContactDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ContactDetailsDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IContactDetailsBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailsBusinessImpl implements IContactDetailsBusiness {

    @Override
    public List<ContactDetailsDTO> constructContactDetailsInfo(QuotationModEntity quotationModEntity) {
        List<ContactDetailsDTO> contacts = new ArrayList<>();

        if(!ValidateUtils.stringIsNullOrEmpty(quotationModEntity.getContactEmailDesc())){
            ContactDetailsDTO contactDetailsDTO = createContactDetailPerType(
                    ConstantsUtil.ContactDetailtype.EMAIL,quotationModEntity.getContactEmailDesc());
            contacts.add(contactDetailsDTO);
        }

        if(!ValidateUtils.stringIsNullOrEmpty(quotationModEntity.getCustomerPhoneDesc())){
            ContactDetailsDTO contactDetailsDTO = createContactDetailPerType(
                    ConstantsUtil.ContactDetailtype.MOBILE,quotationModEntity.getCustomerPhoneDesc());
            contacts.add(contactDetailsDTO);
        }

        return contacts;
    }

    private static ContactDetailsDTO createContactDetailPerType(String contactDetailType,String contactData){
        ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();
        ContactDTO contactDTO = new ContactDTO();

        if(ConstantsUtil.ContactDetailtype.EMAIL.equalsIgnoreCase(contactDetailType)){
            contactDTO.setContactDetailType(ConstantsUtil.ContactDetailtype.EMAIL);
            contactDTO.setAddress(contactData);
        }else if(ConstantsUtil.ContactDetailtype.MOBILE.equalsIgnoreCase(contactDetailType)){
            contactDTO.setContactDetailType(ConstantsUtil.ContactDetailtype.MOBILE);
            contactDTO.setNumber(contactData);
        }

        contactDetailsDTO.setContact(contactDTO);

        return contactDetailsDTO;
    }

}
