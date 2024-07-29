package com.levi.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
@Table(name = "schedule_line_entry")
public class ScheduleLineEntry extends BaseModel {

    @Id
    @Column(name = "id")
    //Combination of order, lineItem, schLineItem and material
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_entry_id")
    @Fetch(FetchMode.JOIN)
    private LineEntry lineEntry;

    @Column(name = "sales_document_number")
    private String salesDocumentNumber;

    @Column(name = "line_item")
    private String lineItem;

    @Column(name = "sch_line_item")
    private String schLineItem;

    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "ordered_quantity")
    private Double orderedQuantity;

    @Column(name = "confirmed_quantity")
    private Double confirmedQuantity;

    @Column(name = "shipped_quantity")
    private Double shippedQuantity;

    @Column(name = "size")
    private String size;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "cancel_date")
    private Date cancelDate;

    @Column(name = "requested_delivery_date")
    private Date requestedDeliveryDate;

    @Column(name = "approx_due_In_date")
    private Date approxDueInDate;

    @Column(name = "rejection_reason_code")
    private String rejectionReasonCode;

    @Column(name = "cancelled_sku_qty")
    private Double cancelledSkuQty;

    @Column(name = "unit_of_measurement")
    private String uom;

    @Column(name = "ship_to")
    private String shipTo;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "pac_unconfirmed_quantity")
    private Double pacUnconfirmedQuantity;

    @Column(name = "vir_unconfirmed_quantity")
    private Double virUnconfirmedQuantity;

    @Column(name = "schedule_status")
    private String scheduleStatus;

    @Column(name = "ship_date")
    private Date shipDate;

    @Column(name = "invoice_doc")
    private String invoiceDoc;

    @Column(name = "invoice_date")
    private Date invoiceDate;

    @Column(name = "rejection_reason_description")
    private String rejectionReasonDescription;

    @Column(name = "open_qty")
    private Double openQty;

    @Column(name = "fixed_qty")
    private Double fixedQty;

    @Column(name = "del_ind")
    private Boolean delInd;

}
