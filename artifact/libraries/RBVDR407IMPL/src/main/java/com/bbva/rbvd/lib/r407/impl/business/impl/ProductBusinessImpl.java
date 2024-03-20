package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurancedao.join.QuotationJoinQuotationModDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PlanDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.InstallmentPlansDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.CoverageDTO;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.PlanBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.FinancingBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.InstallmentFinancingBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.CoverageBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.AssistanceBO;

import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IProductBusiness;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductBusinessImpl implements IProductBusiness {

    private final ApplicationConfigurationService applicationConfigurationService;

    public ProductBusinessImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public ProductDTO constructProductInfo(ResponsePayloadQuotationDetailBO payload,
                                           QuotationJoinQuotationModDTO responseQuotation) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(responseQuotation.getInsuranceProductType());
        productDTO.setName(payload.getProducto());
        productDTO.setPlans(constructPlansInProduct(payload.getPlan(),responseQuotation));

        return productDTO;
    }

    private List<PlanDTO> constructPlansInProduct(PlanBO planBO, QuotationJoinQuotationModDTO responseQuotation){
        if(planBO != null){
            List<PlanDTO> plans = new ArrayList<>();
            PlanDTO planDTO = new PlanDTO();

            planDTO.setId(responseQuotation.getQuotationMod().getInsuranceModalityType());
            planDTO.setName(responseQuotation.getModality().getInsuranceModalityName());
            planDTO.setIsSelected(Boolean.TRUE);
            planDTO.setTotalInstallment(constructTotalInstallmentFromRimac(planBO));
            planDTO.setInstallmentPlans(constructInstallmentPlanFromRimac(planBO));
            planDTO.setCoverages(constructCoveragesFromRimac(planBO.getCoberturas()));
            planDTO.setBenefits(constructBenefitsFromRimac(planBO.getAsistencias()));

            plans.add(planDTO);

            return plans;
        }else{
            return Collections.emptyList();
        }
    }

    private static AmountDTO constructTotalInstallmentFromRimac(PlanBO planBO){
        AmountDTO totalInstallment = new AmountDTO();
        totalInstallment.setAmount(planBO.getPrimaBruta().doubleValue());
        totalInstallment.setCurrency(planBO.getMoneda());

        return totalInstallment;
    }

    private List<InstallmentPlansDTO> constructInstallmentPlanFromRimac(PlanBO planBO){
        List<InstallmentPlansDTO> installmentPlansDTOS = new ArrayList<>();

        FinancingBO financingBO = planBO.getFinanciamientos().stream().filter(
                        financing -> ConstantsUtil.FinancingPeriodicity.ANUAL.equalsIgnoreCase(financing.getPeriodicidad()))
                .findFirst().orElse(null);

        if(financingBO != null){
            InstallmentPlansDTO installmentPlan = new InstallmentPlansDTO();

            installmentPlan.setPaymentsTotalNumber(financingBO.getNumeroCuotas());
            installmentPlan.setPaymentAmount(createPaymentAmount(
                    financingBO.getCuotasFinanciamiento(),planBO.getMoneda()));
            installmentPlan.setPeriod(createPeriod(financingBO.getPeriodicidad()));

            installmentPlansDTOS.add(installmentPlan);
        }

        return installmentPlansDTOS;
    }

    private AmountDTO createPaymentAmount(List<InstallmentFinancingBO> cuotasFinanciamiento, String moneda){
        if(CollectionUtils.isEmpty(cuotasFinanciamiento)){
            return null;
        }else{
            AmountDTO paymentAmount = new AmountDTO();
            paymentAmount.setAmount(cuotasFinanciamiento.get(0).getMonto().doubleValue());
            paymentAmount.setCurrency(moneda);

            return paymentAmount;
        }
    }

    private DescriptionDTO createPeriod(String periodicity){
        DescriptionDTO period = new DescriptionDTO();
        period.setId(this.applicationConfigurationService.getProperty(periodicity));
        period.setName(periodicity.toUpperCase());

        return period;
    }

    private List<CoverageDTO> constructCoveragesFromRimac(List<CoverageBO> coberturas){
        if(CollectionUtils.isEmpty(coberturas)){
            return Collections.emptyList();
        }else{
            return coberturas.stream().map(this::convertCoverage).collect(Collectors.toList());
        }
    }

    private CoverageDTO convertCoverage(CoverageBO cobertura) {
        CoverageDTO coverageDTO = new CoverageDTO();
        coverageDTO.setId(cobertura.getCobertura().toString());
        coverageDTO.setName(cobertura.getDescripcionCobertura());
        coverageDTO.setDescription(cobertura.getObservacionCobertura());
        coverageDTO.setCoverageType(getCoverageTypeFromRimac(cobertura));

        return coverageDTO;
    }

    private DescriptionDTO getCoverageTypeFromRimac(CoverageBO cobertura) {
        DescriptionDTO coverageType = new DescriptionDTO();
        String coverageId = this.applicationConfigurationService.getProperty(
                ConstantsUtil.StringConstants.COVERAGE_TYPE_PREFIX + cobertura.getCondicion());
        String coverageName = this.applicationConfigurationService.getProperty(
                cobertura.getCondicion() + ConstantsUtil.StringConstants.COVERAGE_NAME_SUFFIX);
        coverageType.setId(coverageId);
        coverageType.setName(coverageName);
        return coverageType;
    }

    private List<DescriptionDTO> constructBenefitsFromRimac(List<AssistanceBO> asistencias){
        if(CollectionUtils.isEmpty(asistencias)){
            return Collections.emptyList();
        }else{
            return asistencias.stream().map(this::convertBenefit).collect(Collectors.toList());
        }
    }

    private DescriptionDTO convertBenefit(AssistanceBO asistencia) {
        DescriptionDTO benefit = new CoverageDTO();
        benefit.setId(asistencia.getAsistencia().toString());
        benefit.setName(asistencia.getDescripcionAsistencia());

        return benefit;
    }

}
