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

    public static boolean mapNotContainsNullValue(Map<String,Object> mapa){
        for(Map.Entry<String,Object> entry : mapa.entrySet()){
            if(entry.getValue() == null){
                return false;
            }
        }
        return true;
    }

}
