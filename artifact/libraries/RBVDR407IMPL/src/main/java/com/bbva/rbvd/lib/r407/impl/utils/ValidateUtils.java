package com.bbva.rbvd.lib.r407.impl.utils;

import java.util.Map;

public class ValidateUtils {

    private ValidateUtils(){}

    public static boolean stringIsNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }

    public static boolean mapIsNullOrEmpty(Map<?,?> mapa){
        return mapa == null || mapa.isEmpty();
    }

}
