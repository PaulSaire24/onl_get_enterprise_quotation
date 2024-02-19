package com.bbva.rbvd.lib.r407.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.lib.r407.utils.ContansUtils;

public class BusinessAgentDAO {
    public DescriptionDTO MockBDResponseBA(){
        DescriptionDTO businessAgentResponse = new DescriptionDTO();
        businessAgentResponse.setName(ContansUtils.mockInternalData.BUSINESS_AGENT_NAME);
        return businessAgentResponse;
    }
}
