package com.levi.lambda.common.dao.util;

import com.levi.wholesale.lambda.common.dto.OrderData;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OrderDetailsDataUtil {

    public static OrderData getOrderData() throws ParseException {
        OrderData orderData = new OrderData();
        orderData.setSalesDocumentNumber("57555361");
        orderData.setPurchaseOrderNumber("POJ00304467");
        orderData.setProductCode("34964-0073");
        orderData.setCustomerName("LS RETAIL LEVI ONLINE");
        orderData.setBrand("LEVIS");
        orderData.setConsumerGroup("MEN");
        orderData.setItemCategory("BOTTOMS");
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        java.util.Date date = DateFor.parse("17/12/21 0:00");
        orderData.setSalesDocumentDate(new Date(date.getTime()));
        orderData.setRequestedDeliveryDate(new Date(date.getTime()));
        orderData.setCancelDate(new Date(date.getTime()));
        orderData.setApproxDueDate(new Date(date.getTime()));
        orderData.setCurrency("USD");
        orderData.setStatus("Approved");
        orderData.setTax(10.0);
        orderData.setOrderType("Sale");
        orderData.setPoType("Test");
        orderData.setOrderReason("J0");
        orderData.setInvoiceNumber("10001");
        orderData.setShipTo("Home Addr");
        orderData.setTrackingNumber("20012");
        orderData.setDeliveryDate(new Date(date.getTime()));
        orderData.setDiscount(20.0);
        orderData.setProductName("WEDGIE STRAIGHT JAZZ JIVE SOUND");
        orderData.setPlant("Plant1");
        orderData.setStockType("MENs");
        orderData.setQuantity(1d);
        orderData.setAmount(100.0);
        orderData.setVasAdd("Test Add");
        orderData.setOffPrice(90.0);
        orderData.setExcessInvDis(5.0);
        orderData.setLateShipDis(4.0);
        orderData.setPreBookDis(2.0);
        orderData.setCustomerPrice(110.0);
        orderData.setWholesalePrice(90.0);
        orderData.setLeviPrice(150.0);
        orderData.setDescription("");
        orderData.setVir(324.0);
        orderData.setShipTo("Home Addr");
        orderData.setConfirmedQty(10d);
        orderData.setShippedQuantity(10d);
        orderData.setTotalPrice(100.0);
        orderData.setUnitPrice(10.0);
        orderData.setTotalPrice(100.0);
        orderData.setSubtotal(90.0);
        orderData.setLineItem("LineItem");
        orderData.setSchLineItem("SchLineItem");
        orderData.setMaterialName("Mat1");
        orderData.setLineItemStatus("TEST");
        orderData.setDelInd(true);
        orderData.setRejectedQty(1d);
        orderData.setRejectionReason("J0");
        orderData.setMaterialCode("test_material_code");
        return orderData;
    }
}
