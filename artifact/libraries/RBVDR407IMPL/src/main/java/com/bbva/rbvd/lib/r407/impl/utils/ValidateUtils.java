package com.bbva.rbvd.lib.r407.impl.utils;

import java.util.List;
import java.util.Map;

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

}
