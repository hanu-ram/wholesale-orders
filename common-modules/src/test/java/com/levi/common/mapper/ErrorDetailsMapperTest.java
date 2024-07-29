package com.levi.common.mapper;

import com.levi.common.model.ErrorDetails;
import com.levi.common.utility.TestOrderData;
import com.levi.wholesale.common.dto.OrderData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorDetailsMapperTest {

    @InjectMocks
    private ErrorDetailsMapper errorDetailsMapper;

    @Test
    void testMapToModel() {
        OrderData orderData = TestOrderData.getTestOrderData();
        ErrorDetails errorDetails = new ErrorDetails();
        ErrorDetails model = errorDetailsMapper.mapToModel(orderData, errorDetails);

        assertFields(orderData, model);
    }

    @Test
    void testMapToModel_savesNewError() {
        OrderData orderData = TestOrderData.getTestOrderData();
        ErrorDetails model = errorDetailsMapper.mapToModel(orderData, null);

        assertFields(orderData, model);
    }

    private static void assertFields(OrderData orderData, ErrorDetails model) {

        assertEquals(orderData.getSalesDocumentNumber(), model.getSalesDocumentNumber());
        assertEquals(orderData.getSalesOrganization(), model.getSalesOrg());
        assertEquals(orderData.getSoldTo(), model.getSoldTo());
        assertEquals(orderData.getSalesDocumentType(), model.getSalesDocumentType());
        assertEquals(orderData.getSalesDocumentDate(), model.getSalesDocumentDate());
        assertEquals(orderData.getPoType(), model.getPurchaseOrderType());
        assertEquals(orderData.getPurchaseOrderNumber(), model.getPurchaseOrderNumber());
        assertEquals(orderData.getPlanningGroup(), model.getPlanningGroup());
        assertEquals(orderData.getOrderReason(), model.getOrderReason());
        assertEquals(orderData.getRegion(), model.getRegion());
        assertEquals(orderData.getCountryCode(), model.getCountryCode());
        assertEquals(orderData.getCustomerName(), model.getCustomerName());
        assertEquals(orderData.getStatus(), model.getOrderStatus());
        assertEquals(orderData.getTax(), model.getTax());
        assertEquals(orderData.getAmount(), model.getAmount());
        assertEquals(orderData.getInvoiceNumber(), model.getInvoiceNumber());
        assertEquals(orderData.getShipTo(), model.getShipTo());
        assertEquals(orderData.getTrackingNumber(), model.getTrackingNumber());
        assertEquals(orderData.getDeliveryDate(), model.getDeliveryDate());
        assertEquals(orderData.getDiscount(), model.getDiscount());

        assertEquals(orderData.getMaterialCode(), model.getMaterialCode());
        assertEquals(orderData.getLineItem(), model.getLineItem());
        assertEquals(orderData.getItemCategory(), model.getItemCategory());
        assertEquals(orderData.getConsumerGroup(), model.getConsumerGroup());
        assertEquals(orderData.getMaterialName(), model.getMaterialName());
        assertEquals(orderData.getPlant(), model.getPlant());
        assertEquals(orderData.getStockType(), model.getStockType());
        assertEquals(orderData.getBrand(), model.getBrand());
        assertEquals(orderData.getCurrency(), model.getCurrency());
        assertEquals(orderData.getWholesalePrice(), model.getWholesalePrice());
        assertEquals(orderData.getWholesalePriceValidFrom(), model.getWholesalePriceValidFrom());
        assertEquals(orderData.getWholesalePriceValidTo(), model.getWholesalePriceValidTo());
        assertEquals(orderData.getDiscounts(), model.getDiscounts());
        assertEquals(orderData.getGrossValue(), model.getGrossValue());
        assertEquals(orderData.getExpectedPriceEdi(), model.getExpectedPriceEdi());
        assertEquals(orderData.getLeviRetailPrice(), model.getLeviRetailPrice());
        assertEquals(orderData.getCustomerExpcMsrp(), model.getCustomerExpcMsrp());
        assertEquals(orderData.getRpmPrice(), model.getRpmPrice());
        assertEquals(orderData.getNetValue(), model.getNetValue());
        assertEquals(orderData.getNetPrice(), model.getNetPrice());
        assertEquals(orderData.getQuantity(), model.getQuantity());
        assertEquals(orderData.getDescription(), model.getDescription());
        assertEquals(orderData.getConfirmedQty(), model.getConfirmedQuantity());
        assertEquals(orderData.getShippedQuantity(), model.getShippedQuantity());
        assertEquals(orderData.getSubtotal(), model.getSubTotal());
        assertEquals(orderData.getLineItemStatus(), model.getLineItemStatus());
        assertEquals(orderData.getDelInd(), model.getDelInd());
        assertEquals(orderData.getRejectedQty(), model.getCancelledQuantity());

        assertEquals(orderData.getSchLineItem(), model.getSchLineItem());
        assertEquals(orderData.getOrderedQuantity(), model.getOrderedQuantity());
        assertEquals(orderData.getConfirmedQty(), model.getConfirmedQuantity());
        assertEquals(orderData.getMaterialSize(), model.getSize());
        assertEquals(orderData.getCancelDate(), model.getCancelDate());
        assertEquals(orderData.getRequestedDeliveryDate(), model.getRequestedDeliveryDate());
        assertEquals(orderData.getApproxDueDate(), model.getApproxDueInDate());
        assertEquals(orderData.getRejectionReason(), model.getRejectionReasonCode());
        assertEquals(orderData.getCancelledSkuQty(), model.getCancelledSkuQty());
        assertEquals(orderData.getUom(), model.getUom());
        assertEquals(orderData.getStoreName(), model.getStoreName());
        assertEquals(orderData.getPacUnconfirmedQuantity(), model.getPacUnconfirmedQuantity());
        assertEquals(orderData.getVirUnconfirmedQuantity(), model.getVirUnconfirmedQuantity());
        assertEquals(orderData.getScheduleStatus(), model.getScheduleStatus());
        assertEquals(orderData.getShipDate(), model.getShipDate());
        assertEquals(orderData.getInvoiceDoc(), model.getInvoiceDoc());
        assertEquals(orderData.getInvoiceDate(), model.getInvoiceDate());
        assertEquals(orderData.getRejectionReasonDescription(), model.getRejectionReasonDescription());
        assertEquals(orderData.getOpenQty(), model.getOpenQty());
        assertEquals(orderData.getFixedQty(), model.getFixedQty());
    }
}