package com.bbva.rbvd.lib.r407.impl.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConvertUtils {

    private ConvertUtils(){}


    public static Date convertStringDateToDate(String dateStr){
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate convertStringDateToLocalDate(String dateStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr,formatter);
    }

    public static String convertStringToUpperAndLowerCase(String value){
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase();
    }

    public static BigDecimal getBigDecimalValue(Object value){
        BigDecimal ret = null;
        if(value != null){
            if(value instanceof BigDecimal){
                ret = (BigDecimal) value;
            }else if(value instanceof String){
                ret = new BigDecimal((String) value);
            }else if(value instanceof Double){
                ret = BigDecimal.valueOf(((Double) value));
            }else if(value instanceof Integer){
                ret = BigDecimal.valueOf((Integer) value);
            }
        }

        return ret;
    }


}
