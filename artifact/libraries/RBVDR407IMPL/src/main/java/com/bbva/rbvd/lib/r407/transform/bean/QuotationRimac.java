package com.bbva.rbvd.lib.r407.transform.bean;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.*;
import com.bbva.rbvd.dto.enterpriseinsurance.createquotation.dto.CreateQuotationDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.GetQuotationDTO;
import com.bbva.rbvd.lib.r407.service.dao.PlanDAO;
import com.bbva.rbvd.lib.r407.service.impl.ConsumerInternalService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QuotationRimac {
    private QuotationRimac() {
    }

    public static GetQuotationDTO mapInQuotationResponse(GetQuotationDTO input,String quotationId) {
        GetQuotationDTO response = input;
        PlanDAO planDAO = new PlanDAO();
        ConsumerInternalService consumerInternalService = new ConsumerInternalService();
        DescriptionDTO businessAgentResponse = consumerInternalService.getBusinessAgent("P021322");
        List<ParticipantDTO> participantResponse= listParticipants(input);
        ProductDTO productResponse = consumerInternalService.getProduct("403");
        response.setParticipantDTO(participantResponse);
        response.setBusinessAgent(businessAgentResponse);
        response.setProduct(productResponse);
        response.getProduct().setPlans(planDAO.getPlanInfo());
        response.setId(quotationId);
        response.setEmployees(listEmployees());
        return response;
    }
    public static List<ParticipantDTO> listParticipants(GetQuotationDTO input){
        List<ParticipantDTO> listaParticipantes;
        ConsumerInternalService consumerInternalService = new ConsumerInternalService();
        listaParticipantes = new ArrayList<>();
        ParticipantDTO nombreParticipante;
        nombreParticipante=consumerInternalService.getParticipantInformation("P400212");
        listaParticipantes.add(nombreParticipante);


        return listaParticipantes;
    }
    public static EmployeesDTO listEmployees(){
        EmployeesDTO employees = new EmployeesDTO();
       employees.setEmployeesNumber(new Long(200));
        employees.setAreMajorityAge(false);
        employees.setMonthlyPayrollAmount(new AmountDTO());
        employees.getMonthlyPayrollAmount().setAmount(new BigDecimal(50000));
        employees.getMonthlyPayrollAmount().setCurrency("PEN");
        return employees;
    }
}
