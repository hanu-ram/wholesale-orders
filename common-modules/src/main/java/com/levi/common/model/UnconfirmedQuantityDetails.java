package com.levi.common.model;

import java.sql.Timestamp;
import java.util.Currency;

public interface UnconfirmedQuantityDetails {

    String getLineEntryId();

    Double getVirUnconfirmedQuantity();

    Double getPacUnconfirmedQuantity();

    Double getUnconfirmedQuantity();

    Timestamp getRequestedDeliveryDate();

    Timestamp getCancelDate();

    String getSalesDocumentNumber();

    String getSoldTo();

    Timestamp getSalesDocumentDate();

    String getPurchaseOrderNumber();

    String getBrand();

    String getConsumerGroup();

    String getItemCategoryDescription();

    String getItemDescription();

    Double getNetPrice();

    String getStockType();

    Currency getCurrency();

    String getPlant();

    String getMaterialCode();

    String getLineItem();
}
