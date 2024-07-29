package com.levi.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "error_details")
public class ErrorDetails extends BaseModel {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "sales_document_number")
    private String salesDocumentNumber;

    @Column(name = "sales_org")
    private String salesOrg;

    @Column(name = "sold_to")
    private String soldTo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "brand")
    private String brand;

    @Column(name = "sales_document_date")
    private Date salesDocumentDate;

    @Column(name = "currency")
    private String currency;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "tax")
    private Double tax;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "sales_document_type")
    private String salesDocumentType;

    @Column(name = "purchase_order_type")
    private String purchaseOrderType;

    @Column(name = "order_reason")
    private String orderReason;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "ship_to")
    private String shipTo;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "region")
    private String region;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "line_item")
    private String lineItem;

    @Column(name = "item_category")
    private String itemCategory;

    @Column(name = "consumer_group")
    private String consumerGroup;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "plant")
    private String plant;

    @Column(name = "stock_type")
    private String stockType;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "wholesale_price")
    private Double wholesalePrice;


    @Column(name = "description")
    private String description;

    @Column(name = "confirmed_quantity")
    private Double confirmedQuantity;

    @Column(name = "shipped_quantity")
    private Double shippedQuantity;

    @Column(name = "sub_total")
    private Double subTotal;

    @Column(name = "line_item_status")
    private String lineItemStatus;

    @Column(name = "del_ind")
    private Boolean delInd;

    @Column(name = "cancelled_quantity")
    private Double cancelledQuantity;

    @Column(name = "sch_line_item")
    private String schLineItem;

    @Column(name = "ordered_quantity")
    private Double orderedQuantity;

    @Column(name = "size")
    private String size;

    @Column(name = "schedule_status")
    private String scheduleStatus;

    @Column(name = "approx_due_In_date")
    private Date approxDueInDate;

    @Column(name = "ship_date")
    private Date shipDate;

    @Column(name = "invoice_doc")
    private String invoiceDoc;

    @Column(name = "invoice_date")
    private Date invoiceDate;

    @Column(name = "rejection_reason_code")
    private String rejectionReasonCode;

    @Column(name = "rejection_reason_description")
    private String rejectionReasonDescription;

    @Column(name = "cancelled_sku_qty")
    private Double cancelledSkuQty;

    @Column(name = "open_qty")
    private Double openQty;

    @Column(name = "fixed_qty")
    private Double fixedQty;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "cancel_date")
    private Date cancelDate;

    @Column(name = "unit_of_measurement")
    private String uom;

    @Column(name = "requested_delivery_date")
    private Date requestedDeliveryDate;

    @Column(name = "error_msg")
    private String errorMessage;

    @Column(name = "planning_group")
    private String planningGroup;

    @Column(name = "wholesale_price_valid_from")
    private Date wholesalePriceValidFrom;

    @Column(name = "wholesale_price_valid_to")
    private Date wholesalePriceValidTo;

    @Column(name = "discounts")
    private Double discounts;

    @Column(name = "gross_value")
    private Double grossValue;

    @Column(name = "expected_price_edi")
    private Double expectedPriceEdi;

    @Column(name = "levi_retail_price")
    private Double leviRetailPrice;

    @Column(name = "customer_expc_msrp")
    private Double customerExpcMsrp;

    @Column(name = "rpm_price")
    private Double rpmPrice;

    @Column(name = "net_value")
    private Double netValue;

    @Column(name = "net_price")
    private Double netPrice;

    @Column(name = "pac_unconfirmed_quantity")
    private Double pacUnconfirmedQuantity;

    @Column(name = "vir_unconfirmed_quantity")
    private Double virUnconfirmedQuantity;

    @Column(name = "is_processed")
    private Boolean isProcessed;
}
