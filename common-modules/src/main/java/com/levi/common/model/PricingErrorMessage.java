package com.levi.common.model;

import java.sql.Timestamp;


public interface PricingErrorMessage {

    String getSize();

    Timestamp getRequestedDeliveryDate();

    Timestamp getCancelDate();

    String getItemDescription();

    String getCurrency();

    String getSoldTo();

    String getSalesDocumentNumber();

    Timestamp getSalesDocumentDate();

    String getPurchaseOrderNumber();

    String getPlanningGroup();

    String getBrand();

    String getItemCategoryDescription();

    String getConsumerGroup();

    String getMaterialCode();

    Double getWholesalePrice();

    Timestamp getWholesalePriceValidFrom();

    Timestamp getWholesalePriceValidTo();

    Double getDiscounts();

    Double getGrossValue();

    String getExpectedPriceEdi();

    Double getOrderValue();

    String getLeviRetailPrice();

    String getCustomerExpcMsrp();

    String getRpmPrice();

    Double getQuantity();

    String getLineItem();

    String getScheduleLineItem();
}
