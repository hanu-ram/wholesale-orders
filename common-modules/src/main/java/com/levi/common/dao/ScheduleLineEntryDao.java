package com.levi.common.dao;

import com.levi.common.constant.QueryConstants;
import com.levi.common.utils.CommonUtils;
import com.levi.wholesale.common.dto.OrderData;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import static com.levi.common.constant.Constants.SYSTEM_USER;

@Repository
public class ScheduleLineEntryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveWithQuery(List<OrderData> orderDataList) {
        Timestamp currentTime = Timestamp.valueOf(CommonUtils.getUtcDateTime());

        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryConstants.SCH_LINE_ENTRY_UPSERT)) {

                for (OrderData orderData : orderDataList) {
                    preparedStatement.setString(1, getSchLineEntryId(orderData));
                    preparedStatement.setString(2, getLineEntryId(orderData));
                    preparedStatement.setString(3, orderData.getSalesDocumentNumber());
                    preparedStatement.setString(4, orderData.getLineItem());
                    preparedStatement.setString(5, orderData.getSchLineItem());
                    preparedStatement.setString(6, orderData.getMaterialCode());
                    preparedStatement.setDouble(7, orderData.getQuantity());
                    preparedStatement.setDouble(8, orderData.getConfirmedQty());
                    preparedStatement.setDouble(9, orderData.getShippedQuantity());
                    preparedStatement.setString(10, orderData.getMaterialSize());
                    preparedStatement.setDate(11, orderData.getDeliveryDate());
                    preparedStatement.setDate(12, orderData.getCancelDate());
                    preparedStatement.setDate(13, orderData.getRequestedDeliveryDate());
                    preparedStatement.setDate(14, orderData.getApproxDueDate());
                    preparedStatement.setString(15, orderData.getRejectionReason());
                    preparedStatement.setDouble(16, orderData.getCancelledSkuQty());
                    preparedStatement.setString(17, orderData.getUom());
                    preparedStatement.setString(18, orderData.getShipTo());
                    preparedStatement.setString(19, orderData.getStoreName());
                    preparedStatement.setDouble(20, orderData.getPacUnconfirmedQuantity());
                    preparedStatement.setDouble(21, orderData.getVirUnconfirmedQuantity());
                    preparedStatement.setString(22, orderData.getScheduleStatus());
                    preparedStatement.setDate(23, orderData.getShipDate());
                    preparedStatement.setString(24, orderData.getInvoiceDoc());
                    preparedStatement.setDate(25, orderData.getInvoiceDate());
                    preparedStatement.setString(26, orderData.getRejectionReasonDescription());
                    preparedStatement.setDouble(27, orderData.getOpenQty());
                    preparedStatement.setDouble(28, orderData.getFixedQty());
                    preparedStatement.setBoolean(29, orderData.getDelInd());
                    preparedStatement.setTimestamp(30, currentTime);
                    preparedStatement.setString(31, SYSTEM_USER);
                    preparedStatement.setTimestamp(32, currentTime);
                    preparedStatement.setString(33, SYSTEM_USER);

                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        });
        session.flush();
    }

    private static String getLineEntryId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem() + "-" + orderData.getMaterialCode();
    }

    private static String getSchLineEntryId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem() + "-" + orderData.getMaterialCode();
    }


}
