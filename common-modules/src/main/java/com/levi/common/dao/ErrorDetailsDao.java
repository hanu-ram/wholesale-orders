package com.levi.common.dao;

import com.levi.common.constant.QueryConstants;
import com.levi.common.model.ErrorDetails;
import com.levi.common.utils.CommonUtils;
import com.levi.wholesale.common.dto.OrderData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Timestamp;

import static com.levi.common.constant.Constants.SYSTEM_USER;

@Repository
public class ErrorDetailsDao {

    @PersistenceContext
    private EntityManager entityManager;

    public String saveWithQuery(OrderData orderData, String message, boolean isProcessed) {
        Query nativeQuery = entityManager.createNativeQuery(QueryConstants.ERROR_UPSERT, ErrorDetails.class);
        String id = getErrorId(orderData);

        Timestamp currentTime = Timestamp.valueOf(CommonUtils.getUtcDateTime());

        nativeQuery.setParameter(1, id);
        nativeQuery.setParameter(2, orderData.getSalesDocumentNumber());
        nativeQuery.setParameter(3, orderData.getSalesOrganization());
        nativeQuery.setParameter(4, orderData.getSoldTo());
        nativeQuery.setParameter(5, orderData.getCustomerName());
        nativeQuery.setParameter(6, orderData.getPurchaseOrderNumber());
        nativeQuery.setParameter(7, orderData.getBrand());
        nativeQuery.setParameter(8, orderData.getSalesDocumentDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(9, orderData.getCurrency());
        nativeQuery.setParameter(10, orderData.getStatus());
        nativeQuery.setParameter(11, orderData.getTax());
        nativeQuery.setParameter(12, orderData.getAmount());
        nativeQuery.setParameter(13, orderData.getSalesDocumentType());
        nativeQuery.setParameter(14, orderData.getPoType());
        nativeQuery.setParameter(15, orderData.getOrderReason());
        nativeQuery.setParameter(16, orderData.getInvoiceNumber());
        nativeQuery.setParameter(17, orderData.getShipTo());
        nativeQuery.setParameter(18, orderData.getTrackingNumber());
        nativeQuery.setParameter(19, orderData.getDeliveryDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(20, orderData.getDiscount());
        nativeQuery.setParameter(21, orderData.getRegion());
        nativeQuery.setParameter(22, orderData.getCountryCode());
        nativeQuery.setParameter(23, orderData.getMaterialCode());
        nativeQuery.setParameter(24, orderData.getLineItem());
        nativeQuery.setParameter(25, orderData.getItemCategory());
        nativeQuery.setParameter(26, orderData.getConsumerGroup());
        nativeQuery.setParameter(27, orderData.getMaterialName());
        nativeQuery.setParameter(28, orderData.getPlant());
        nativeQuery.setParameter(29, orderData.getStockType());
        nativeQuery.setParameter(30, orderData.getQuantity());
        nativeQuery.setParameter(31, orderData.getWholesalePrice());
        nativeQuery.setParameter(32, orderData.getDescription());
        nativeQuery.setParameter(33, orderData.getConfirmedQty());
        nativeQuery.setParameter(34, orderData.getShippedQuantity());
        nativeQuery.setParameter(35, orderData.getSubtotal());
        nativeQuery.setParameter(36, orderData.getLineItemStatus());
        nativeQuery.setParameter(37, orderData.getDelInd());
        nativeQuery.setParameter(38, orderData.getRejectedQty());
        nativeQuery.setParameter(39, orderData.getSchLineItem());
        nativeQuery.setParameter(40, orderData.getOrderedQuantity());
        nativeQuery.setParameter(41, orderData.getMaterialSize());
        nativeQuery.setParameter(42, orderData.getScheduleStatus());
        nativeQuery.setParameter(43, orderData.getApproxDueDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(44, orderData.getShipDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(45, orderData.getInvoiceDoc());
        nativeQuery.setParameter(46, orderData.getInvoiceDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(47, orderData.getRejectionReason());
        nativeQuery.setParameter(48, orderData.getRejectionReasonDescription());
        nativeQuery.setParameter(49, orderData.getCancelledSkuQty());
        nativeQuery.setParameter(50, orderData.getOpenQty());
        nativeQuery.setParameter(51, orderData.getFixedQty());
        nativeQuery.setParameter(52, orderData.getStoreName());
        nativeQuery.setParameter(53, orderData.getCancelDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(54, orderData.getUom());
        nativeQuery.setParameter(55, orderData.getRequestedDeliveryDate(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(56, message);
        nativeQuery.setParameter(57, orderData.getPlanningGroup());
        nativeQuery.setParameter(58, orderData.getWholesalePriceValidFrom(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(59, orderData.getWholesalePriceValidTo(), TemporalType.TIMESTAMP);
        nativeQuery.setParameter(60, orderData.getDiscounts());
        nativeQuery.setParameter(61, orderData.getGrossValue());
        nativeQuery.setParameter(62, orderData.getExpectedPriceEdi());
        nativeQuery.setParameter(63, orderData.getLeviRetailPrice());
        nativeQuery.setParameter(64, orderData.getCustomerExpcMsrp());
        nativeQuery.setParameter(65, orderData.getRpmPrice());
        nativeQuery.setParameter(66, orderData.getNetValue());
        nativeQuery.setParameter(67, orderData.getNetPrice());
        nativeQuery.setParameter(68, orderData.getPacUnconfirmedQuantity());
        nativeQuery.setParameter(69, orderData.getVirUnconfirmedQuantity());
        nativeQuery.setParameter(70, isProcessed);
        nativeQuery.setParameter(71, currentTime);
        nativeQuery.setParameter(72, SYSTEM_USER);
        nativeQuery.setParameter(73, currentTime);
        nativeQuery.setParameter(74, SYSTEM_USER);

        nativeQuery.executeUpdate();

        return id;
    }

    private static String getErrorId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem() + "-" + orderData.getMaterialCode();
    }
}
