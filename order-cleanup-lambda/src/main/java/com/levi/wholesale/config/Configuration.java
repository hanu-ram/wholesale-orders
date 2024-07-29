package com.levi.wholesale.config;

public final class Configuration {

    private Configuration() {

    }

    public static String getRetentionPeriod() {
        return System.getenv("retentionThreshold");
    }

    public static String getPageSize() {
        return System.getenv("pageSize");
    }
    public static String getMaxThreadNumber() {
        return System.getenv("maxThreadNumber");
    }

}
