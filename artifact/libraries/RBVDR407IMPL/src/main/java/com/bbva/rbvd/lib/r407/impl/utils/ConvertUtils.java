package com.bbva.rbvd.lib.r407.impl.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConvertUtils {

    public static String getRequestJsonFormat(final Object requestBody) {
        return JsonUtils.getInstance().serialization(requestBody);
    }

    public static Date convertStringDateToDate(String dateStr){
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


}
