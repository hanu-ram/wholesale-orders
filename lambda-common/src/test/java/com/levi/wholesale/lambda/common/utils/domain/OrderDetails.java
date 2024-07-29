package com.levi.wholesale.lambda.common.utils.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class OrderDetails {

    @CsvBindByName(column = "Sold-to")
    private String soldTo;
    @CsvBindByName(column = "Sales Organization")
    private String salesOrganization;
    @CsvBindByName(column = "Purchase Order Number")
    private String purchaseOrderNumber;
    @CsvBindByName(column = "Sales Document Number", required = true)
    private String salesDocumentNumber;
    @CsvBindByName(column = "Sales Document Date")
    private String salesDocumentDate;
    @CsvBindByName(column = "Sales Document Type")
    private String salesDocumentType;
    @CsvBindByName(column = "Purchase Order Type")
    private String poType;
    @CsvBindByName(column = "Order Reason")
    private String orderReason;
    @CsvBindByName(column = "Planning Group")
    private String planningGroup;
    @CsvBindByName(column = "Region")
    private String region;
    @CsvBindByName(column = "Country Code")
    private String countryCode;
    @CsvBindByName(column = "Material Code", required = true)
    private String materialCode;
    @CsvBindByName(column = "Line Item", required = true)
    private String lineItem;
    @CsvBindByName(column = "Brand")
    private String brand;
    @CsvBindByName(column = "Currency")
    private String currency;
    @CsvBindByName(column = "Item Category")
    private String itemCategory;
    @CsvBindByName(column = "Consumer Group")
    private String consumerGroup;
    @CsvBindByName(column = "Material Name")
    private String materialName;
    @CsvBindByName(column = "Plant")
    private String plant;
    @CsvBindByName(column = "Stock Type")
    private String stockType;
    @CsvBindByName(column = "Customer Expc. MSRP")
    private Double cusomerExpcMsrp;
    @CsvBindByName(column = "Wholesale Price")
    private Double wholesalePrice;
    @CsvBindByName(column = "Wholesale Price Valid From")
    private String wholesalePriceValidFrom;
    @CsvBindByName(column = "Wholesale Price Valid To")
    private String wholesalePriceValidTo;
    @CsvBindByName(column = "Net Value")
    private Double netValue;
    @CsvBindByName(column = "LS&Co Retail Price")
    private Double leviRetailPrice;
    @CsvBindByName(column = "Expected Price EDI")
    private Double expectedPriceEdi;
    @CsvBindByName(column = "Gross Value ")
    private Double grossValue;
    @CsvBindByName(column = "RPM Price")
    private Double rpmPrice;
    @CsvBindByName(column = "Net Price")
    private Double netPrice;
    @CsvBindByName(column = "Discounts")
    private Double discounts;
    @CsvBindByName(column = "Sch Line Item", required = true)
    private String schLineItem;
    @CsvBindByName(column = "Material Size")
    private String materialSize;
    @CsvBindByName(column = "Quantity")
    private Double quantity;
    @CsvBindByName(column = "Confirmed Qty")
    private Double confirmedQty;
    @CsvBindByName(column = "Shipped Qty")
    private Double shippedQty;
    @CsvBindByName(column = "Requested Delivery Date")
    private String requestedDeliveryDate;
    @CsvBindByName(column = "Cancel Date")
    private String cancelDate;
    @CsvBindByName(column = "Delivery Date")
    private String deliveryDate;
    @CsvBindByName(column = "Approx Due Date")
    private String approxDueDate;
    @CsvBindByName(column = "Ship-to")
    private String shipTo;
    @CsvBindByName(column = "Store Name")
    private String storeName;
    @CsvBindByName(column = "Unit Of Measurement")
    private String unitOfMeasurement;
    @CsvBindByName(column = "Rejected Qty")
    private String rejectedQty;
    @CsvBindByName(column = "VIR Unconfirmed Quantity")
    private Double virUnconfirmedQuantity;
    @CsvBindByName(column = "PAC Unconfirmed Quantity")
    private Double pacUnconfirmedQuantity;
    @CsvBindByName(column = "Rejection Reason")
    private String rejectionReason;

}
