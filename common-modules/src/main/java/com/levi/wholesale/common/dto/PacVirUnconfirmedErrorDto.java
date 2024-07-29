package com.levi.wholesale.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PacVirUnconfirmedErrorDto {

    @JsonProperty("sold_to")
    private String soldTo;
    @JsonProperty("sales_document_number")
    private String salesDocumentNumber;
    @JsonProperty("sales_document_date")
    private String salesDocumentDate;
    @JsonProperty("purchase_order_number")
    private String purchaseOrderNumber;
    @JsonProperty("material_code")
    private String materialCode;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("consumer_group")
    private String consumerGroup;
    @JsonProperty("item_category_description")
    private String itemCategoryDescription;
    @JsonProperty("requested_delivery_date")
    private String requestedDeliveryDate;
    @JsonProperty("cancel_date")
    private String cancelDate;
    @JsonProperty("item_description")
    private String itemDescription;
    @JsonProperty("plant")
    private String plant;
    @JsonProperty("currency")
    private Currency currency;
    @JsonProperty("pac_unconfirmed_quantity")
    private Double pacUnconfirmedQuantity;
    @JsonProperty("vir_unconfirmed_quantity")
    private Double virUnconfirmedQuantity;
    @JsonProperty("unconfirmed_quantity")
    private Double unconfirmedQuantity;
    @JsonProperty("stock_type")
    private String stockType;
    @JsonProperty("order_value")
    private Double orderValue;
    @JsonProperty("line_item")
    private String lineItem;

}
