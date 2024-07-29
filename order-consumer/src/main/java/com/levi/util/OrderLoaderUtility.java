package com.levi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.levi.common.constant.Constants.OC_INVALID_DATE_FORMAT;
import static com.levi.common.constant.Constants.OC_PARSING_FAILURE_ALERT;

@Component
@Slf4j
public class OrderLoaderUtility {

    private final ObjectMapper objectMapper;

    public OrderLoaderUtility(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderData getOrderDataFromJsonString(String value) {
        OrderData orderData = null;
        try {
            orderData = objectMapper.readValue(value, OrderData.class);
        } catch (JsonProcessingException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("expected format \"yyyyMMdd\"")) {
                log.error(OC_INVALID_DATE_FORMAT
                        + " : Invalid date format for some of the fields, expected date format is \"yyyyMMdd\" for JSON : {}", value, ex);
            } else {
                log.error(OC_PARSING_FAILURE_ALERT + ": Failed when deserializing the JSON into POJO : {}", value, ex);
            }
        }
        return orderData;
    }
}
