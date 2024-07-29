package com.levi.common.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"salesDocumentNumber"}, callSuper = true)
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
@Table(name = "order_details")
public class OrderDetails extends BaseModel {

    @Id
    @Column(name = "sales_document_number")
    private String salesDocumentNumber;

    @Column(name = "sales_org")
    private String salesOrg;

    @Column(name = "sold_to")
    private String soldTo;

    @Column(name = "sales_document_type")
    private String salesDocumentType;

    @Column(name = "sales_document_date")
    private Date salesDocumentDate;

    @Column(name = "purchase_order_type")
    private String purchaseOrderType;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "planning_group")
    private String planningGroup;

    @Column(name = "order_reason")
    private String orderReason;

    @Column(name = "region")
    private String region;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "tax")
    private Double tax;

    @Column(name = "amount")
    private Double amount;

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

    @JsonIgnore
    @OneToMany(targetEntity = LineEntry.class, mappedBy = "orderDetails",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    private Set<LineEntry> lineEntries;

    @Column(name = "is_valid")
    private Boolean isValid;
}
