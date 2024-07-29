package com.levi.common.utility;

import com.levi.wholesale.common.dto.OrderData;

import java.sql.Date;
import java.time.LocalDate;

public class TestOrderData {

    public static OrderData getTestOrderData() {
        OrderData orderData = new OrderData();

        /* OrderDetails fields  */
        orderData.setSalesDocumentNumber("test_sales_document_number");
        orderData.setSalesOrganization("test_sales_org");
        orderData.setSoldTo("test_sold_to");
        orderData.setSalesDocumentType("test_sales_document_type");
        orderData.setSalesDocumentDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setPoType("test_purchase_order_type");
        orderData.setPurchaseOrderNumber("test_purchase_order_number");
        orderData.setPlanningGroup("test_planning_group");
        orderData.setOrderReason("test_order_reason");
        orderData.setRegion("test_region");
        orderData.setCountryCode("test_country_code");
        orderData.setCustomerName("test_customer_name");
        orderData.setStatus("test_order_status");
        orderData.setTax(5d);
        orderData.setAmount(100d);
        orderData.setInvoiceNumber("test_invoice_number");
        orderData.setShipTo("test_ship_to");
        orderData.setTrackingNumber("test_tracking_number");
        orderData.setDeliveryDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setDiscount(5d);
        orderData.setCustomerExpcMsrp(5.0);

        /* LineEntry fields */
        orderData.setMaterialCode("tes_material_code");
        orderData.setLineItem("test_line_item");
        orderData.setItemCategory("test_item_category");
        orderData.setConsumerGroup("test_consumer_group");
        orderData.setMaterialName("test_material_name");
        orderData.setPlant("test_plant");
        orderData.setStockType("test_stock_type");
        orderData.setBrand("test_brand");
        orderData.setCurrency("USD");
        orderData.setWholesalePrice(5d);
        orderData.setWholesalePriceValidFrom(Date.valueOf(LocalDate.of(2023, 1, 21)));
        orderData.setWholesalePriceValidTo(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setDiscounts(5d);
        orderData.setGrossValue(100d);
        orderData.setExpectedPriceEdi(5d);
        orderData.setLeviRetailPrice(5d);
        orderData.setCustomerExpcMsrp(5d);
        orderData.setRpmPrice(5d);
        orderData.setNetValue(100d);
        orderData.setNetPrice(5d);
        orderData.setQuantity(5d);
        orderData.setDescription("test_description");
        orderData.setConfirmedQty(5d);
        orderData.setShippedQuantity(5d);
        orderData.setSubtotal(5d);
        orderData.setLineItemStatus("tes_line_item_status");
        orderData.setDelInd(true);
        orderData.setRejectedQty(5d);

        /* SkuDetails fields*/
        orderData.setSchLineItem("test_sch_line_item");
        orderData.setOrderedQuantity(5d);
        orderData.setConfirmedQty(5d);
        orderData.setMaterialSize("test_size");
        orderData.setCancelDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setRequestedDeliveryDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setApproxDueDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setRejectionReason("test_rejection_reason");
        orderData.setCancelledSkuQty(5d);
        orderData.setUom("unit_of_measurement");
        orderData.setStoreName("store_name");
        orderData.setPacUnconfirmedQuantity(5d);
        orderData.setVirUnconfirmedQuantity(5d);
        orderData.setScheduleStatus("schedule_status");
        orderData.setShipDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setInvoiceDoc("invoice_doc");
        orderData.setInvoiceDate(Date.valueOf(LocalDate.of(2023, 1, 31)));
        orderData.setRejectionReasonDescription("tes_rejection_reason_description");
        orderData.setOpenQty(5d);
        orderData.setFixedQty(5d);

        return orderData;
    }
}
