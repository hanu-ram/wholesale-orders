package com.levi.common.constant;

public final class Constants {
    private Constants() {
    }

    public static final String SYSTEM_USER = "SYSTEM";
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final long HOURS = 24;

    public static final String WHOLESALE_PAC_PROCESSING_FAILED = "wholesale_pac_processing_failed";
    public static final String WHOLESALE_VIR_PROCESSING_FAILED = "wholesale_vir_processing_failed";
    public static final String WHOLESALE_PE_PROCESSING_FAILED = "wholesale_pe_processing_failed";
    public static final String WHOLESALE_CX_NO_DATA_FOUND = "wholesale_outbound_data_not_found";
    public static final String OC_DB_SAVE_FAILURE_ALERT = "oc_db_save_failed";
    public static final String OC_MISSING_MANDATORY_FIELDS = "oc_missing_mandatory_fields";
    public static final String OC_NEGATIVE_QUANTITY_FIELDS = "oc_negative_quantity_fields";
    public static final String OC_PROCESS_FAILURE_ALERT = "oc_processing_failed";
    public static final String OC_PARSING_FAILURE_ALERT = "oc_parsing_failed";
    public static final String OC_ERR_TOPIC_ALERT = "oc_sent_to_err_topic";
    public static final String OC_INVALID_DATE_FORMAT = "oc_invalid_date_format";
}
