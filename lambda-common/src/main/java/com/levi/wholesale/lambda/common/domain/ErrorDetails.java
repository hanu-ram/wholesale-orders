package com.levi.wholesale.lambda.common.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ErrorDetails {
    private String id;

    private String salesDocumentNumber;

    private String salesOrg;

    private String soldTo;

    private String customerName;

    private String purchaseOrderNumber;

    private String brand;

    private Date salesDocumentDate;

    private String currency;

    private String orderStatus;

    private Double tax;

    private Double amount;

    private String salesDocumentType;

    private String purchaseOrderType;

    private String orderReason;

    private String invoiceNumber;

    private String shipTo;

    private String trackingNumber;

    private Date deliveryDate;

    private Double discount;

    private String region;

    private String countryCode;

    private String materialCode;

    private String lineItem;

    private String itemCategory;

    private String consumerGroup;

    private String plant;

    private String stockType;

    private Double quantity;

    private Double wholesalePrice;

    private Double ipa;

    private String description;

    private Double confirmedQuantity;

    private Double shippedQuantity;

    private Double subTotal;

    private String lineItemStatus;

    private Boolean delInd;

    private Double cancelledQuantity;

    private String schLineItem;

    private Double orderedQuantity;

    private String size;

    private String scheduleStatus;

    private Date approxDueInDate;

    private Date shipDate;

    private String invoiceDoc;

    private Date invoiceDate;

    private String rejectionReasonCode;

    private String rejectionReasonDescription;

    private Double cancelledSkuQty;

    private Double openQty;

    private Double fixedQty;

    private String storeName;

    private Date cancelDate;

    private String uom;

    private Date requestedDeliveryDate;

    private String errorMessage;

    private String planningGroup;

    private Date wholesalePriceValidFrom;

    private Date wholesalePriceValidTo;

    private Double discounts;
    private Double grossValue;
    private Double expectedPriceEdi;
    private Double leviRetailPrice;
    private Double cusomerExpcMsrp;
    private Double rpmPrice;
    private Double netValue;
    private Double netPrice;
    private Double pacUnconfirmedQuantity;
    private Double virUnconfirmedQuantity;
    private Boolean isProcessed;
    private Timestamp createTimestamp;
    private String createdBy;
    private Timestamp modifiedTimestamp;
    private String modifiedBy;
}
