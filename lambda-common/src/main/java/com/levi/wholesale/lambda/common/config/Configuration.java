package com.levi.wholesale.lambda.common.config;

public final class Configuration {

    private Configuration() {

    }

    public static String getDBUrl() {
        return System.getenv("DB_URL");
    }

    public static String getDBUserName() {
        return System.getenv("DB_USERNAME");
    }

    public static String getDriverName() {
        return System.getenv("DB_DRIVER");
    }

    public static String getDBPassword() {
        return System.getenv("DB_PASSWORD");
    }

    public static String getDBName() {
        return System.getenv("DB_NAME");
    }

    public static String getKafkaTopic() {
        return System.getenv("KAFKA_TOPIC");
    }

    public static String getKafkaErrorTopic() {
        return System.getenv("WHOLESALE_ERROR_TOPIC");
    }

    public static Boolean isFileMoveFeatureEnabled() {
        return Boolean.valueOf(System.getenv("FILE_MOVE_FEATURE_ENABLED"));
    }

    public static String getProcessedFileFolderName() {
        return System.getenv("PROCESSED_FILES_FOLDER");
    }

    public static String getFailedFileFolderName() {
        return System.getenv("FAILED_FILES_FOLDER");
    }

    public static String getBootStrapHost() {
        return System.getenv("BOOTSTRAP_HOST_PORT");
    }

    public static String getGroupId() {
        return System.getenv("GROUP_ID");
    }

    public static String getProducerAck() {
        return System.getenv("PRODUCER_ACK");
    }

    public static String getProducerRetries() {
        return System.getenv("PRODUCER_RETRIES");
    }

    public static String getApiKey() {
        return System.getenv("API_KEY");
    }

    public static String getApiSecret() {
        return System.getenv("API_SECRET");
    }

    public static String shouldUseSSL() {
        return System.getenv("USE_SSL");
    }

    public static String shouldUseSASL() {
        return System.getenv("USE_SASL");
    }

    public static String getKeyStoreLocation() {
        return System.getenv("KEY_STORE_LOCATION");
    }

    public static String getKeyStorePassword() {
        return System.getenv("KEY_STORE_PASSWORD");
    }

    public static String getKeyPassword() {
        return System.getenv("KEY_STORE_PASSWORD");
    }

    public static String getSecretName() {
        return System.getenv("SECRET_NAME");
    }

    public static String getArchiveFolderName() {
        return System.getenv("ARCHIVE_FOLDER");
    }

    public static String getErrorName() {
        return System.getenv("ERROR_FOLDER");
    }

    public static String getSecretKey() {
        return System.getenv("SECRET_KEY");
    }

    public static String getSecretRegion() {
        return System.getenv("SECRET_REGION");
    }

    public static String getGDOBucketName() {
        return System.getenv("GDO_BUCKET_NAME");
    }

    public static String getGDOBucketPrefixName() {
        return System.getenv("GDO_BUCKET_PREFIX");
    }

    public static String getDestinationPrefixName() {
        return System.getenv("DESTINATION_PREFIX");
    }

    public static String getRetryTopic() {
        return System.getenv("WHOLESALE_ORDER_RETRY");
    }

    public static String getPollTimeOut() {
        return System.getenv("KAFKA_CONSUMER_POLL_TIMEOUT");
    }

    public static Integer getMaxRetryAttempts() {
        return Integer.parseInt(System.getenv("MAX_RETRY_ATTEMPTS"));
    }
}
