package com.bbva.rbvd.lib.r407.service.impl;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.*;
import com.bbva.rbvd.dto.enterpriseinsurance.createquotation.rimac.QuotationBO;
import com.bbva.rbvd.dto.enterpriseinsurance.createquotation.rimac.QuotationResponseBO;
import com.bbva.rbvd.dto.enterpriseinsurance.modifyquotation.rimac.InstallmentFinancingBO;
import com.bbva.rbvd.lib.r407.utils.ContansUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsumerExternalService {
public QuotationResponseBO callRimacService (QuotationResponseBO payloadRimac){
    QuotationResponseBO responseRimac;
    responseRimac = mockDataRimac();
    return responseRimac;
}
public QuotationResponseBO mockDataRimac(){
    QuotationResponseBO responseRimacMock = new QuotationResponseBO();
    QuotationBO cotizacion = new QuotationBO();
    cotizacion.setCotizacion(ContansUtils.mockData.COTIZACION);
    cotizacion.setFechaFinVigencia(ContansUtils.mockData.FECHA_FIN_VIGENCIA);
    cotizacion.setDiasVigencia(ContansUtils.mockData.DIAS_VIGENCIA);
    PlanBO plan1=new PlanBO();
    plan1.setPlan(ContansUtils.mockData.PLAN);
    plan1.setDescripcionPlan(ContansUtils.mockData.DESCRIPTION_PLAN);
    plan1.setPrimaNeta(ContansUtils.mockData.PRIMA_NETA);
    AssistanceBO asistencias = new AssistanceBO();
    asistencias.setAsistencia(ContansUtils.mockData.ASISTENCIA);
    asistencias.setDescripcionAsistencia(ContansUtils.mockData.DESCRIPTION_ASISTENCIA);
    List<AssistanceBO> listaAsistencias = new ArrayList<>();
    listaAsistencias.add(asistencias);
    plan1.setAsistencias(listaAsistencias);
    FinancingBO financiamientos = new FinancingBO();
    financiamientos.setFinanciamiento(ContansUtils.mockData.FINANCIAMIENTO);
    financiamientos.setPeriodicidad(ContansUtils.mockData.PERIODICIDAD);
    financiamientos.setNumeroCuotas(ContansUtils.mockData.NUMERO_CUOTAS);
    financiamientos.setFechaInicio(ContansUtils.mockData.FECHA_INICIO);
    InstallmentFinancingBO cuotasFinanciamiento = new InstallmentFinancingBO();
    cuotasFinanciamiento.setCuota(ContansUtils.mockData.CUOTA);
    cuotasFinanciamiento.setMonto(ContansUtils.mockData.MONTO);
    cuotasFinanciamiento.setFechaVencimiento(ContansUtils.mockData.FECHA_VENCIMIENTO);
    List<InstallmentFinancingBO> listaFinanciamiento = new ArrayList<>();
    listaFinanciamiento.add(cuotasFinanciamiento);
    financiamientos.setCuotasFinanciamiento(listaFinanciamiento);
    List<FinancingBO> listaFinancias = new ArrayList<>();
    listaFinancias.add(financiamientos);
    plan1.setFinanciamientos(listaFinancias);
    CoverageBO coberturas = new CoverageBO();
    coberturas.setCobertura(ContansUtils.mockData.COBERTURAS);
    coberturas.setDescripcionCobertura(ContansUtils.mockData.DESCRIPCION_COBERTURA);
    coberturas.setPrimaNeta(ContansUtils.mockData.PRIMA_NETA_COBERTURA);
    coberturas.setMoneda(ContansUtils.mockData.MONEDA);
    coberturas.setSumaAsegurada(ContansUtils.mockData.SUMA_ASEGURADA);
    coberturas.setObservacionCobertura(ContansUtils.mockData.OBSERVACION_COBERTURA);
    coberturas.setCondicion(ContansUtils.mockData.CONDITION);
    List<CoverageBO> listaCoberturas = new ArrayList<>();
    listaCoberturas.add(coberturas);
    plan1.setCoberturas(listaCoberturas);
    List<PlanBO> listaPlanes = new ArrayList<>();
    listaPlanes.add(plan1);
    cotizacion.setPlanes(listaPlanes);
    responseRimacMock.setCotizaciones(cotizacion);
    responseRimacMock.setProducto(ContansUtils.mockData.PRODUCTO);
    responseRimacMock.setMoneda(ContansUtils.mockData.MONEDA);
    List<PlanBO> listaPlanesCotizacion =new ArrayList<>();
    responseRimacMock.setPlanes(listaPlanesCotizacion);
    ParticularDataBO datosParticulares = new ParticularDataBO();
    datosParticulares.setEtiqueta(ContansUtils.mockData.ETIQUETA);
    datosParticulares.setCodigo(ContansUtils.mockData.CODIGO);
    datosParticulares.setValor(ContansUtils.mockData.VALOR);

    responseRimacMock.setDatosParticulares(datosParticulares);
    FinancingBO financiamiento = new FinancingBO();
    financiamiento.setFinanciamiento(ContansUtils.mockData.FINANCIAMIENTO);
    financiamiento.setNumeroCuotas(ContansUtils.mockData.NUMERO_CUOTAS);
    responseRimacMock.setFinanciamiento(financiamiento);
    return responseRimacMock;
}
}
