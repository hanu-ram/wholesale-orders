package com.levi.common.dao;

import com.levi.wholesale.common.dto.OrderData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorDetailsDaoTest {

    @InjectMocks
    private ErrorDetailsDao errorDetailsDao;

    @Mock
    private EntityManager entityManager;

    @Test
    void TestSaveWithQuery() {
        OrderData orderData = getOrderData();
        Query query = mock(Query.class);

        String expectedErrorId = "test-sales-document-number"
                + "-" + "test-line-item"
                + "-" + "test-sch-line-item"
                + "-" + "test-material-code";
        String message = "test-error-message";
        boolean isProcessed = false;

        when(entityManager.createNativeQuery(anyString(), any(Class.class))).thenReturn(query);

        String actualErrorId = errorDetailsDao.saveWithQuery(orderData, message, isProcessed);

        verify(query).executeUpdate();
        verifySetParameter(query, orderData, expectedErrorId, message, isProcessed);
        assertEquals(expectedErrorId, actualErrorId);
    }

    private static void verifySetParameter(Query query, OrderData orderData, String id, String message, boolean isProcessed) {

        verify(query).setParameter(1, id);
        verify(query).setParameter(2, orderData.getSalesDocumentNumber());
        verify(query).setParameter(3, orderData.getSalesOrganization());
        verify(query).setParameter(4, orderData.getSoldTo());
        verify(query).setParameter(5, orderData.getCustomerName());
        verify(query).setParameter(6, orderData.getPurchaseOrderNumber());
        verify(query).setParameter(7, orderData.getBrand());
        verify(query).setParameter(8, orderData.getSalesDocumentDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(9, orderData.getCurrency());
        verify(query).setParameter(10, orderData.getStatus());
        verify(query).setParameter(11, orderData.getTax());
        verify(query).setParameter(12, orderData.getAmount());
        verify(query).setParameter(13, orderData.getSalesDocumentType());
        verify(query).setParameter(14, orderData.getPoType());
        verify(query).setParameter(15, orderData.getOrderReason());
        verify(query).setParameter(16, orderData.getInvoiceNumber());
        verify(query).setParameter(17, orderData.getShipTo());
        verify(query).setParameter(18, orderData.getTrackingNumber());
        verify(query).setParameter(19, orderData.getDeliveryDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(20, orderData.getDiscount());
        verify(query).setParameter(21, orderData.getRegion());
        verify(query).setParameter(22, orderData.getCountryCode());
        verify(query).setParameter(23, orderData.getMaterialCode());
        verify(query).setParameter(24, orderData.getLineItem());
        verify(query).setParameter(25, orderData.getItemCategory());
        verify(query).setParameter(26, orderData.getConsumerGroup());
        verify(query).setParameter(27, orderData.getMaterialName());
        verify(query).setParameter(28, orderData.getPlant());
        verify(query).setParameter(29, orderData.getStockType());
        verify(query).setParameter(30, orderData.getQuantity());
        verify(query).setParameter(31, orderData.getWholesalePrice());
        verify(query).setParameter(32, orderData.getDescription());
        verify(query).setParameter(33, orderData.getConfirmedQty());
        verify(query).setParameter(34, orderData.getShippedQuantity());
        verify(query).setParameter(35, orderData.getSubtotal());
        verify(query).setParameter(36, orderData.getLineItemStatus());
        verify(query).setParameter(37, orderData.getDelInd());
        verify(query).setParameter(38, orderData.getRejectedQty());
        verify(query).setParameter(39, orderData.getSchLineItem());
        verify(query).setParameter(40, orderData.getOrderedQuantity());
        verify(query).setParameter(41, orderData.getMaterialSize());
        verify(query).setParameter(42, orderData.getScheduleStatus());
        verify(query).setParameter(43, orderData.getApproxDueDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(44, orderData.getShipDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(45, orderData.getInvoiceDoc());
        verify(query).setParameter(46, orderData.getInvoiceDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(47, orderData.getRejectionReason());
        verify(query).setParameter(48, orderData.getRejectionReasonDescription());
        verify(query).setParameter(49, orderData.getCancelledSkuQty());
        verify(query).setParameter(50, orderData.getOpenQty());
        verify(query).setParameter(51, orderData.getFixedQty());
        verify(query).setParameter(52, orderData.getStoreName());
        verify(query).setParameter(53, orderData.getCancelDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(54, orderData.getUom());
        verify(query).setParameter(55, orderData.getRequestedDeliveryDate(), TemporalType.TIMESTAMP);
        verify(query).setParameter(56, message);
        verify(query).setParameter(57, orderData.getPlanningGroup());
        verify(query).setParameter(58, orderData.getWholesalePriceValidFrom(), TemporalType.TIMESTAMP);
        verify(query).setParameter(59, orderData.getWholesalePriceValidTo(), TemporalType.TIMESTAMP);
        verify(query).setParameter(60, orderData.getDiscounts());
        verify(query).setParameter(61, orderData.getGrossValue());
        verify(query).setParameter(62, orderData.getExpectedPriceEdi());
        verify(query).setParameter(63, orderData.getLeviRetailPrice());
        verify(query).setParameter(64, orderData.getCustomerExpcMsrp());
        verify(query).setParameter(65, orderData.getRpmPrice());
        verify(query).setParameter(66, orderData.getNetValue());
        verify(query).setParameter(67, orderData.getNetPrice());
        verify(query).setParameter(68, orderData.getPacUnconfirmedQuantity());
        verify(query).setParameter(69, orderData.getVirUnconfirmedQuantity());
        verify(query).setParameter(70, isProcessed);
        verify(query, times(2)).setParameter(anyInt(), any(Timestamp.class));
        verify(query).setParameter(72, "SYSTEM");
        verify(query).setParameter(74, "SYSTEM");

    }

    private static OrderData getOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSalesDocumentNumber("test-sales-document-number");
        orderData.setMaterialCode("test-material-code");
        orderData.setLineItem("test-line-item");
        orderData.setSchLineItem("test-sch-line-item");
        return orderData;
    }
}