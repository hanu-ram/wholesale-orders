package com.levi.wholesale.lambda.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class CommonUtils {

    private CommonUtils() {
    }

    public static LocalDateTime getUtcDateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        ZonedDateTime zoneTime = ZonedDateTime.ofLocal(now, ZoneId.of("UTC"), null);
        return LocalDateTime.parse(zoneTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public static Matcher getMatcher(String pattern, String name) {

        Pattern r = Pattern.compile(pattern);
        //if file is within directory remove directory path for matching
        return r.matcher(name.contains("/") ? name.substring(name.lastIndexOf("/") + 1) : name);
    }

    public static boolean validateFilePattern(String pattern, String name) {
        Matcher matcher = getMatcher(pattern, name);
        if (!matcher.find()) {
            throw new RuntimeException(String.format("%s does not match with pattern %s", name, pattern));
        }
        return true;
    }

}
