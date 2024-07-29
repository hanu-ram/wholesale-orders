package com.levi.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.Currency;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"lineEntryId"}, callSuper = true)
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
@Table(name = "line_entry")
public class LineEntry extends BaseModel {

    @Id
    @Column(name = "line_entry_id")
    private String lineEntryId; //order-lineItem-materialCode

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"sales_document_number\"")
    @Fetch(FetchMode.JOIN)
    private OrderDetails orderDetails;

    @Column(name = "material_code", nullable = false)
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

    @Column(name = "brand")
    private String brand;

    @Column(name = "currency")
    private Currency currency;

    @Column(name = "wholesale_price")
    private Double wholesalePrice;

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

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "description")
    private String description;

    @Column(name = "ship_to")
    private String shipTo;

    @Column(name = "confirmed_quantity")
    private Double confirmedQuantity;

    @Column(name = "shipped_quantity")
    private Double shippedQuantity;

    @Column(name = "sub_total")
    private Double subTotal;

    @Column(name = "line_item_status")
    private String lineItemStatus;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "del_ind")
    private Boolean delInd;

    @Column(name = "cancelled_quantity")
    private Double cancelledQuantity;

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleLineEntry.class, mappedBy = "lineEntry",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    private Set<ScheduleLineEntry> scheduleLineEntries;

}
