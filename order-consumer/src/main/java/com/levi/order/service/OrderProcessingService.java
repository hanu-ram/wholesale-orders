package com.levi.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.levi.common.model.ErrorDetails;
import com.levi.common.utils.CommonUtils;
import com.levi.util.OrderLoaderUtility;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.levi.common.constant.Constants.OC_DB_SAVE_FAILURE_ALERT;
import static com.levi.common.constant.Constants.OC_MISSING_MANDATORY_FIELDS;
import static com.levi.common.constant.Constants.OC_NEGATIVE_QUANTITY_FIELDS;

@Service
@Slf4j
public class OrderProcessingService {

    private final OrderDetailsService orderDetailsService;
    private final ErrorDetailsService errorDetailsService;
    private final RetryService retryService;
    private final OrderLoaderUtility orderLoaderUtility;

    @Autowired
    public OrderProcessingService(OrderDetailsService orderDetailsService,
                                  ErrorDetailsService errorDetailsService,
                                  RetryService retryService,
                                  OrderLoaderUtility orderLoaderUtility) {
        this.orderDetailsService = orderDetailsService;
        this.errorDetailsService = errorDetailsService;
        this.retryService = retryService;
        this.orderLoaderUtility = orderLoaderUtility;
    }


    public boolean validateAndPopulateOrderList(String value, List<OrderData> orderDataList) {
        OrderData orderData = orderLoaderUtility.getOrderDataFromJsonString(value);
        if (orderData != null) {
            try {
                String invalidFields = validateOrderData(orderData);
                String negativeQuantities = validateQuantity(orderData);
                if (invalidFields.isEmpty() && negativeQuantities.isEmpty()) {

                    Boolean isValidOrder = processErrorDetails(orderData, false);
                    log.info("isValidOrder: {}, sales document number : {}", isValidOrder, orderData.getSalesDocumentNumber());
                    orderData.setIsValid(isValidOrder);
                    orderDataList.add(orderData);

                } else {
                    handleInvalidFields(orderDataList, orderData, invalidFields, negativeQuantities);
                }
            } catch (Exception exception) {
                log.error(OC_DB_SAVE_FAILURE_ALERT + " : Exception occurred while saving order details, order id : {}, Exception : ",
                        orderData.getSalesDocumentNumber(), exception);
                return false;
            }
        }
        return true;
    }

    private void handleInvalidFields(List<OrderData> orderDataList, OrderData orderData,
                                     String invalidFields, String negativeQuantities) {

        String totalInvalidFields = "";
        if (!invalidFields.isEmpty()) {
            log.error(OC_MISSING_MANDATORY_FIELDS + ": Missing mandatory fields - {}", invalidFields);
            totalInvalidFields += invalidFields;
        }
        if (!negativeQuantities.isEmpty()) {
            log.error(OC_NEGATIVE_QUANTITY_FIELDS + ": Negative quantity fields - {}", negativeQuantities);
            if (!invalidFields.isEmpty()) {
                totalInvalidFields += "," + negativeQuantities;
            } else {
                totalInvalidFields += negativeQuantities;
            }
        }
        ErrorDetails errorDetails = errorDetailsService.getErrorDetails(getErrorId(orderData));
        if (errorDetails != null) {
            errorDetails.setIsProcessed(false);
            errorDetailsService.updateErrorDetails(orderData, errorDetails);
        } else {
            errorDetailsService.saveErrorDetails(orderData, totalInvalidFields);
        }
        orderData.setIsValid(false);
        orderDataList.add(orderData);
        log.info("Invalid data for record having - salesDocumentNumber: {}, "
                        + "lineItem: {}, schLineItem: {}, materialCode: {}", orderData.getSalesDocumentNumber(),
                orderData.getLineItem(), orderData.getSchLineItem(), orderData.getMaterialCode());
        log.info("Error saved successfully");
    }

    public void saveOrderDataList(List<OrderData> orderDataList) throws JsonProcessingException {
        if (!orderDataList.isEmpty()) {
            try {
                log.info("Saving Order data list with number of orders : {} ", orderDataList.size());
                Long startTime = System.currentTimeMillis();
                orderDetailsService.saveOrderData(orderDataList);
                Long endTime = System.currentTimeMillis();
                log.info("Time taken to save order data : {} ", endTime - startTime);
            } catch (Exception e) {
                log.info(OC_DB_SAVE_FAILURE_ALERT + ": Exception occurred while saving order data, Exception : ", e);
                retryService.resendDataToKafkaTopic(orderDataList);
            }
        }
    }


    private boolean processErrorDetails(OrderData orderData, boolean isProcessed) {
        List<ErrorDetails> errorDetails = errorDetailsService.getErrorDetails(orderData.getSalesDocumentNumber(), isProcessed);
        boolean isValidOrder = true;
        for (ErrorDetails errorDetail : errorDetails) {
            log.info("processing error details : {}", errorDetail);
            if (errorDetail.getId().equals(getErrorId(orderData))) {
                errorDetail.setIsProcessed(true);
                log.info("isProcessed set to true");
                errorDetailsService.updateErrorDetails(orderData, errorDetail);
                log.info("error details updated, sales document number : {}", orderData.getSalesDocumentNumber());
            } else {
                isValidOrder = false;
            }
        }
        return isValidOrder;
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

    public String validateQuantity(OrderData orderData) {
        StringBuilder sb = new StringBuilder();

        CommonUtils.validateQuantity(orderData.getQuantity(), "quantity", sb);
        CommonUtils.validateQuantity(orderData.getOrderedQuantity(), "orderedQuantity", sb);
        CommonUtils.validateQuantity(orderData.getShippedQuantity(), "shippedQuantity", sb);
        CommonUtils.validateQuantity(orderData.getConfirmedQty(), "confirmedQty", sb);
        CommonUtils.validateQuantity(orderData.getOpenQty(), "openQty", sb);
        CommonUtils.validateQuantity(orderData.getFixedQty(), "fixedQty", sb);
        CommonUtils.validateQuantity(orderData.getPacUnconfirmedQuantity(), "pacUnconfirmedQuantity", sb);
        CommonUtils.validateQuantity(orderData.getVirUnconfirmedQuantity(), "virUnconfirmedQuantity", sb);
        CommonUtils.validateQuantity(orderData.getCancelledSkuQty(), "cancelledSkuQty", sb);
        CommonUtils.validateQuantity(orderData.getRejectedQty(), "rejectedQty", sb);
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return "";
    }

    private static String getErrorId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem() + "-" + orderData.getMaterialCode();
    }
}
