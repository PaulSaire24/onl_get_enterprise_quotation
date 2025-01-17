package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PlanDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.RateDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DetailRateDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DetailRateUnitDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.InstallmentPlansDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.CoverageDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.PlanBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.TaxBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.CoverageBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.FinancingBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.InstallmentFinancingBO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.AssistanceBO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.rimac.ResponsePayloadQuotationDetailBO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IProductBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductBusinessImpl implements IProductBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductBusinessImpl.class);

    private final ApplicationConfigurationService applicationConfigurationService;

    public ProductBusinessImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public ProductDTO constructProduct(ResponsePayloadQuotationDetailBO payload,
                                           QuotationDAO responseQuotation) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(responseQuotation.getInsuranceProductType());
        productDTO.setName(payload.getProducto());
        productDTO.setPlans(constructPlansInProduct(payload.getPlan(),responseQuotation));

        LOGGER.info("ProductBusinessImpl - constructProduct() - productDTO: {}", productDTO);

        return productDTO;
    }

    private List<PlanDTO> constructPlansInProduct(PlanBO planBO, QuotationDAO responseQuotation){
        PlanDTO planDTO = new PlanDTO();

        planDTO.setId(responseQuotation.getInsuranceModalityType());
        planDTO.setName(responseQuotation.getInsuranceModalityName());
        planDTO.setIsSelected(Boolean.TRUE);
        planDTO.setTotalInstallment(constructAmountFromRimac(planBO.getPrimaBruta(),planBO.getMoneda()));
        planDTO.setInstallmentPlans(constructInstallmentPlanFromRimac(planBO));
        planDTO.setCoverages(constructCoveragesFromRimac(planBO.getCoberturas()));
        planDTO.setBenefits(constructBenefitsFromRimac(planBO.getAsistencias()));
        planDTO.setRates(constructRateFromRimac(planBO.getTasas()));

        LOGGER.info("ProductBusinessImpl - constructPlansInProduct() - planDTO: {}", planDTO);
        return Collections.singletonList(planDTO);
    }

    private static RateDTO constructRateFromRimac(List<TaxBO> tasas){
        return Optional.ofNullable(tasas)
                .filter(t -> !t.isEmpty())
                .map(t -> {
                    RateDTO rateDTO = new RateDTO();
                    List<DetailRateDTO> rates = t.stream().map(tasa -> {
                        DetailRateDTO itemizeRates = new DetailRateDTO();
                        itemizeRates.setRateType("TASA DE PRIMA - " + tasa.getRango());
                        itemizeRates.setDescription(tasa.getDescripcion());
                        DetailRateUnitDTO itemizeRateUnits = new DetailRateUnitDTO();
                        itemizeRateUnits.setUnitType("PERCENTAGE");
                        itemizeRateUnits.setPercentage(tasa.getTasa().doubleValue());
                        itemizeRates.setItemizeRateUnits(Collections.singletonList(itemizeRateUnits));
                        return itemizeRates;
                    }).collect(Collectors.toList());
                    rateDTO.setItemizeRates(rates);
                    return rateDTO;
                }).orElse(null);
    }

    private static AmountDTO constructAmountFromRimac(BigDecimal amount, String currency){
        if(amount != null && currency != null){
            AmountDTO installment = new AmountDTO();
            installment.setAmount(amount.doubleValue());
            installment.setCurrency(currency);

            return installment;
        }
        return null;
    }

    private List<InstallmentPlansDTO> constructInstallmentPlanFromRimac(PlanBO planBO){
        if(!CollectionUtils.isEmpty(planBO.getFinanciamientos())){
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
        return Collections.emptyList();

    }

    private AmountDTO createPaymentAmount(List<InstallmentFinancingBO> cuotasFinanciamiento, String moneda){
        if(CollectionUtils.isEmpty(cuotasFinanciamiento)){
            return null;
        }else{
            return constructAmountFromRimac(cuotasFinanciamiento.get(0).getMonto(),moneda);
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
        coverageDTO.setName(cobertura.getObservacionCobertura());
        coverageDTO.setDescription(cobertura.getNumeroSueldos() + ConstantsUtil.StringConstants.PREFIX_REMUNERATIONS);
        coverageDTO.setCoverageType(getCoverageTypeFromRimac(cobertura));

        return coverageDTO;
    }

    private DescriptionDTO getCoverageTypeFromRimac(CoverageBO cobertura) {
        DescriptionDTO coverageType = new DescriptionDTO();

        String coverageId;
        String coverageName;

        if(ConstantsUtil.StringConstants.S.equalsIgnoreCase(cobertura.getPrincipal())){
            coverageId = this.applicationConfigurationService.getProperty(
                    ConstantsUtil.StringConstants.COVERAGE_TYPE_PREFIX + "OBL");
            coverageName = this.applicationConfigurationService.getProperty(
                    "OBL" + ConstantsUtil.StringConstants.COVERAGE_NAME_SUFFIX);
        }else{
            coverageId = this.applicationConfigurationService.getProperty(
                    ConstantsUtil.StringConstants.COVERAGE_TYPE_PREFIX + "INC");
            coverageName = this.applicationConfigurationService.getProperty(
                    "INC" + ConstantsUtil.StringConstants.COVERAGE_NAME_SUFFIX);
        }

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
