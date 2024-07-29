package com.levi.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public final class CommonUtils {
    private CommonUtils() {
    }

    public static LocalDateTime getUtcDateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        ZonedDateTime zoneTime = ZonedDateTime.ofLocal(now, ZoneId.of("UTC"), null);
        return LocalDateTime.parse(zoneTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public static void validateField(Object value, String fieldName, StringBuilder sb) {
        if (value == null || value instanceof String && ((String) value).isEmpty()) {
            sb.append(fieldName).append(",");
        }
    }

    public static void validateQuantity(Double value, String fieldName, StringBuilder sb) {
        if (value != null && value < 0) {
            sb.append(fieldName).append(",");
        }
    }

    public static String getDateString(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date(timestamp.getTime());
            return sf.format(date);
        }
        return null;
    }

    public static Date getCurrentDate() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = new Date();
        String todayWithZeroTime = formatter.format(today);
        DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        return formatter1.parse(todayWithZeroTime);
    }
}
