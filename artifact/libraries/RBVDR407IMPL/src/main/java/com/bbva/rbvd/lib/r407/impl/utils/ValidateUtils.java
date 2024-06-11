package com.bbva.rbvd.lib.r407.impl.utils;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.rimac.ParticularDataBO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidateUtils {

    private ValidateUtils(){}

    public static boolean stringIsNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }

    public static boolean mapIsNullOrEmpty(Map<?,?> mapa){
        return mapa == null || mapa.isEmpty();
    }

    public static boolean allValuesNotNullOrEmpty(List<Object> values) {
        for (Object value : values) {
            if (value == null) {
                return false;
            }

            if (value instanceof String && ((String) value).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static ParticularDataBO getParticularDataByTag(List<ParticularDataBO> datosParticulares, String etiqueta){
        if(!CollectionUtils.isEmpty(datosParticulares)){
            Optional<ParticularDataBO> filter = datosParticulares.stream()
                    .filter(dato -> etiqueta.equals(dato.getEtiqueta()))
                    .findFirst();
            return filter.orElse(null);
        }else{
            return null;
        }
    }

}
