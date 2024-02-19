package com.bbva.rbvd.lib.r407.service.dao;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.CoverageDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PlanDTO;
import com.bbva.rbvd.lib.r407.utils.ContansUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlanDAO {
    public List<PlanDTO> getPlanInfo(){
        List<PlanDTO> listaPlanes = new ArrayList<>();
        PlanDTO plan = new PlanDTO();
        List<CoverageDTO> listaCoberturas = new ArrayList<>();
        CoverageDTO coberturas= new CoverageDTO();
        AmountDTO totalInstallments = new AmountDTO();
            plan.setId("277303");
            plan.setIsSelected(true);
            plan.setIsRecommended(true);
            totalInstallments.setAmount(BigDecimal.valueOf(ContansUtils.rimacInput.AMOUNT));
            totalInstallments.setCurrency(ContansUtils.rimacInput.CURRENCY);
            plan.setTotalInstallment(totalInstallments);
            coberturas.setId("9675");
            listaCoberturas.add(coberturas);
            plan.setCoverages(listaCoberturas);
            listaPlanes.add(plan);
        return listaPlanes;
    }
}
