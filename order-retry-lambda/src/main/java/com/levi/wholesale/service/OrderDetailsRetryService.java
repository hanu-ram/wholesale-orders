package com.levi.wholesale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.wholesale.lambda.common.config.Configuration;
import com.levi.wholesale.lambda.common.domain.dao.ErrorDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.domain.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.ScheduleLineEntryDao;
import com.levi.wholesale.lambda.common.dto.OrderData;
import com.levi.wholesale.lambda.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class OrderDetailsRetryService {

    private final OrderDetailsDao orderDetailsDao;
    private final LineEntryDao lineEntryDao;
    private final ScheduleLineEntryDao scheduleLineEntryDao;
    private final ErrorDetailsDao errorDetailsDao;
    private final ObjectMapper objectMapper;
    private final ErrorService errorService;

    public OrderDetailsRetryService(OrderDetailsDao orderDetailsDao, LineEntryDao lineEntryDao,
                                    ScheduleLineEntryDao scheduleLineEntryDao, ErrorDetailsDao errorDetailsDao,
                                    ObjectMapper objectMapper, ErrorService errorService) {
        this.orderDetailsDao = orderDetailsDao;
        this.lineEntryDao = lineEntryDao;
        this.scheduleLineEntryDao = scheduleLineEntryDao;
        this.errorDetailsDao = errorDetailsDao;
        this.objectMapper = objectMapper;
        this.errorService = errorService;
    }

    public void saveOrderDetails(String value, Connection connection, int retryCount) {
        OrderData orderData = getOrderDataFromJsonString(value);
        if (orderData != null) {
            try {
                String invalidFields = validateOrderData(orderData);
                if (invalidFields.isEmpty()) {
                    log.info("Saving Order detail : {} ", orderData.getSalesDocumentNumber());
                    Long startTime = System.currentTimeMillis();

                    Boolean isValidOrder = processErrorDetails(connection, orderData, false);
                    log.debug("isValidOrder: {}, sales document number : {}", isValidOrder, orderData.getSalesDocumentNumber());

                    saveOrderData(connection, orderData, isValidOrder);

                    Long endTime = System.currentTimeMillis();
                    log.info("Order details saved in all three tables successfully, sales document number : {} ", orderData.getSalesDocumentNumber());
                    log.info("Time taken to save order details : {} ", endTime - startTime);
                } else {
                    String message = "Missing some of mandatory fields : " + invalidFields;
                    log.error("Missing mandatory fields - {}", invalidFields);
                    orderDetailsDao.saveOrder(connection, orderData, false);
                    errorDetailsDao.saveErrorDetails(connection, orderData, getErrorAndSkuId(orderData), message, false);
                    log.error("Missing fields for record having - salesDocumentNumber: {}, "
                                    + "lineItem: {}, schLineItem: {}, materialCode: {}", orderData.getSalesDocumentNumber(),
                            orderData.getLineItem(), orderData.getSchLineItem(), orderData.getMaterialCode());
                    log.info("Error saved successfully");
                }
                connection.commit();
            } catch (Exception exception) {
                log.info("Exception occurred while saving order details ", exception);
                if (retryCount > Configuration.getMaxRetryAttempts()) {
                    errorService.sendToErrorTopic(value);
                    log.info("All retry attempt exhausted sent to error topic, message : {} ", value);
                } else {
                    retryCount++;
                    saveOrderDetails(value, connection, retryCount);
                }
            }
        }
    }

    private void saveOrderData(Connection connection, OrderData orderData, boolean isValidOrder) throws SQLException {
        orderDetailsDao.saveOrder(connection, orderData, isValidOrder);
        lineEntryDao.saveLineEntry(connection, orderData, getLineEntryId(orderData));
        scheduleLineEntryDao.saveSchLineEntry(connection, orderData, getErrorAndSkuId(orderData));
    }

    private boolean processErrorDetails(Connection connection, OrderData orderData, boolean isProcessed) throws
            SQLException {
        List<String> ids = errorDetailsDao.getErrorDetails(connection, orderData.getSalesDocumentNumber(), isProcessed);
        boolean isValidOrder = true;
        for (String id : ids) {
            log.debug("processing error details : {}", id);
            String errorDetailsId = getErrorAndSkuId(orderData);
            if (id.equals(errorDetailsId)) {
                log.debug("isProcessed set to true");
                errorDetailsDao.saveErrorDetails(connection, orderData, getErrorAndSkuId(orderData), "", true);
                log.debug("Error details record updated with correction, Id : {}", errorDetailsId);
            } else {
                isValidOrder = false;
            }
        }
        return isValidOrder;
    }

    private OrderData getOrderDataFromJsonString(String value) {
        OrderData orderData = null;
        try {
            orderData = objectMapper.readValue(value, OrderData.class);
        } catch (JsonProcessingException ex) {
            log.error("Failed when deserializing the JSON into POJO : ", ex);
        }
        return orderData;
    }

    public String validateOrderData(OrderData orderData) {
        StringBuilder sb = new StringBuilder();
        CommonUtils.validateField(orderData.getSoldTo(), "soldTo", sb);
        CommonUtils.validateField(orderData.getPurchaseOrderNumber(), "purchaseOrderNumber", sb);
        CommonUtils.validateField(orderData.getSalesDocumentNumber(), "salesDocumentNumber", sb);
        CommonUtils.validateField(orderData.getCurrency(), "currency", sb);
        CommonUtils.validateField(orderData.getRegion(), "region", sb);
        CommonUtils.validateField(orderData.getCountryCode(), "countryCode", sb);
        CommonUtils.validateField(orderData.getLineItem(), "lineItem", sb);
        CommonUtils.validateField(orderData.getSchLineItem(), "schLineItem", sb);
        CommonUtils.validateField(orderData.getMaterialSize(), "materialSize", sb);
        CommonUtils.validateField(orderData.getMaterialCode(), "materialCode", sb);

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return "";
    }

    private static String getErrorAndSkuId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem() + "-" + orderData.getMaterialCode();
    }

    private static String getLineEntryId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getMaterialCode();
    }
}
