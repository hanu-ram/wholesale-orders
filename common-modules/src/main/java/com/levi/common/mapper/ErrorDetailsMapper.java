package com.levi.common.mapper;

import com.levi.common.model.ErrorDetails;
import com.levi.common.utils.CommonUtils;
import com.levi.wholesale.common.dto.OrderData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

import static com.levi.common.constant.Constants.SYSTEM_USER;

@Component
public class ErrorDetailsMapper {

    public ErrorDetails mapToModel(OrderData orderData, ErrorDetails errorDetails) {
        if (errorDetails == null) {
            errorDetails = new ErrorDetails();
            errorDetails.setCreateTimestamp(Timestamp.valueOf(CommonUtils.getUtcDateTime()));
        }
        BeanUtils.copyProperties(orderData, errorDetails);
        errorDetails.setId(getErrorId(orderData));
        errorDetails.setSalesOrg(orderData.getSalesOrganization());
        errorDetails.setOrderStatus(orderData.getStatus());
        errorDetails.setAmount(orderData.getAmount());
        errorDetails.setPurchaseOrderType(orderData.getPoType());
        errorDetails.setConfirmedQuantity(orderData.getConfirmedQty());
        errorDetails.setCancelledQuantity(orderData.getRejectedQty());
        errorDetails.setSubTotal(orderData.getSubtotal());
        errorDetails.setSize(orderData.getMaterialSize());
        errorDetails.setApproxDueInDate(orderData.getApproxDueDate());
        errorDetails.setRejectionReasonCode(orderData.getRejectionReason());
        errorDetails.setCreatedBy(SYSTEM_USER);
        errorDetails.setModifiedBy(SYSTEM_USER);
        errorDetails.setModifiedTimestamp(Timestamp.valueOf(CommonUtils.getUtcDateTime()));

        return errorDetails;
    }

    private static String getErrorId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem() + "-" + orderData.getMaterialCode();
    }
}
