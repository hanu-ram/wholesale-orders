package com.levi.wholesale.lambda.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrderData {

    @JsonProperty("sold_to")
    private String soldTo;
    @JsonProperty("purchase_order_number")
    private String purchaseOrderNumber;
    @JsonProperty("sales_document_number")
    private String salesDocumentNumber;
    @JsonProperty("product_code")
    private String productCode;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("item_category")
    private String itemCategory;
    @JsonProperty("sales_document_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date salesDocumentDate;
    @JsonProperty("requested_delivery_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date requestedDeliveryDate;
    @JsonProperty("cancel_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date cancelDate;
    @JsonProperty("approx_due_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date approxDueDate;
    @JsonProperty("plant")
    private String plant;
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("consumer_group")
    private String consumerGroup;

    @JsonProperty("customer_price")
    private Double customerPrice = 0.0;
    @JsonProperty("wholesale_price")
    private Double wholesalePrice = 0.0;
    @JsonProperty("levi_price")
    private Double leviPrice;
    @JsonProperty("stock_type")
    private String stockType;
    @JsonProperty("vir")
    private Double vir = 0.0;
    @JsonProperty("quantity")
    private Double quantity = 0.0;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("amount")
    private Double amount = 0.0;
    @JsonProperty("rejection_reason")
    private String rejectionReason;
    @JsonProperty("status")
    private String status;
    @JsonProperty("tax")
    private Double tax = 0.0;
    @JsonProperty("order_type")
    private String orderType;
    @JsonProperty("po_type")
    private String poType;
    @JsonProperty("order_reason")
    private String orderReason;
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("ship_to")
    private String shipTo;
    @JsonProperty("tracking_number")
    private String trackingNumber;
    @JsonProperty("delivery_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date deliveryDate;
    @JsonProperty("discount")
    private Double discount = 0.0;
    @JsonProperty("vas_add")
    private String vasAdd;
    @JsonProperty("off_price")
    private Double offPrice = 0.0;
    @JsonProperty("excess_inv_dis")
    private Double excessInvDis = 0.0;
    @JsonProperty("late_ship_dis")
    private Double lateShipDis = 0.0;
    @JsonProperty("pre_book_dis")
    private Double preBookDis = 0.0;
    @JsonProperty("description")
    private String description;
    @JsonProperty("confirmed_qty")
    private Double confirmedQty = 0.0;
    @JsonProperty("shipped_quantity")
    private Double shippedQuantity = 0.0;
    @JsonProperty("unit_price")
    private Double unitPrice = 0.0;
    @JsonProperty("total_price")
    private Double totalPrice = 0.0;
    @JsonProperty("sub_total")
    private Double subtotal = 0.0;
    @JsonProperty("line_item_status")
    private String lineItemStatus;
    @JsonProperty("del_ind")
    private Boolean delInd = false;
    @JsonProperty("line_item")
    private String lineItem;
    @JsonProperty("sch_line_item")
    private String schLineItem;
    @JsonProperty("material_name")
    private String materialName;
    @JsonProperty("ordered_quantity")
    private Double orderedQuantity = 0.0;
    @JsonProperty("material_size")
    private String materialSize;
    @JsonProperty("schedule_status")
    private String scheduleStatus;
    @JsonProperty("ship_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date shipDate;
    @JsonProperty("invoice_doc")
    private String invoiceDoc;
    @JsonProperty("invoice_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date invoiceDate;
    @JsonProperty("rejection_reason_description")
    private String rejectionReasonDescription;
    @JsonProperty("cancelled_sku_qty")
    private Double cancelledSkuQty = 0.0;
    @JsonProperty("open_qty")
    private Double openQty = 0.0;
    @JsonProperty("fixed_qty")
    private Double fixedQty = 0.0;
    @JsonProperty("region")
    private String region;
    @JsonProperty("sales_organization")
    private String salesOrganization;
    @JsonProperty("sales_document_type")
    private String salesDocumentType;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("material_code")
    private String materialCode;
    @JsonProperty("rejected_qty")
    private Double rejectedQty = 0.0;
    @JsonProperty("unit_of_measurement")
    private String uom;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("planning_group")
    private String planningGroup;
    @JsonProperty("wholesale_price_valid_from")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date wholesalePriceValidFrom;
    @JsonProperty("wholesale_price_valid_to")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private Date wholesalePriceValidTo;
    @JsonProperty("discounts")
    private Double discounts = 0.0;
    @JsonProperty("gross_value")
    private Double grossValue = 0.0;
    @JsonProperty("expected_price_edi")
    private Double expectedPriceEdi = 0.0;
    @JsonProperty("levi_retail_price")
    private Double leviRetailPrice = 0.0;
    @JsonProperty("customer_expc_msrp")
    private Double customerExpcMsrp = 0.0;
    @JsonProperty("rpm_price")
    private Double rpmPrice = 0.0;
    @JsonProperty("net_value")
    private Double netValue = 0.0;
    @JsonProperty("net_price")
    private Double netPrice = 0.0;
    @JsonProperty("pac_unconfirmed_quantity")
    private Double pacUnconfirmedQuantity = 0.0;
    @JsonProperty("vir_unconfirmed_quantity")
    private Double virUnconfirmedQuantity = 0.0;
    private Boolean isValid = true;
    private Integer retryCount = 0;
}
