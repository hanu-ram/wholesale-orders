package com.levi.common.dao;

import com.levi.common.utils.CommonUtils;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import static com.levi.common.constant.Constants.SYSTEM_USER;
import static com.levi.common.constant.QueryConstants.ORDER_DETAILS_UPSERT;

@Repository
@Slf4j
public class OrderDetailsDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveWithQuery(List<OrderData> orderDataList) {
        Timestamp currentTime = Timestamp.valueOf(CommonUtils.getUtcDateTime());

        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(ORDER_DETAILS_UPSERT)) {
                for (OrderData orderData : orderDataList) {
                    preparedStatement.setString(1, orderData.getSalesDocumentNumber());
                    preparedStatement.setString(2, orderData.getSalesOrganization());
                    preparedStatement.setString(3, orderData.getSoldTo());
                    preparedStatement.setString(4, orderData.getSalesDocumentType());
                    preparedStatement.setDate(5, orderData.getSalesDocumentDate());
                    preparedStatement.setString(6, orderData.getPoType());
                    preparedStatement.setString(7, orderData.getPurchaseOrderNumber());
                    preparedStatement.setString(8, orderData.getPlanningGroup());
                    preparedStatement.setString(9, orderData.getOrderReason());
                    preparedStatement.setString(10, orderData.getRegion());
                    preparedStatement.setString(11, orderData.getCountryCode());
                    preparedStatement.setString(12, orderData.getCustomerName());
                    preparedStatement.setString(13, orderData.getStatus());
                    preparedStatement.setDouble(14, orderData.getTax());
                    preparedStatement.setDouble(15, orderData.getAmount());
                    preparedStatement.setString(16, orderData.getInvoiceNumber());
                    preparedStatement.setString(17, orderData.getTrackingNumber());
                    preparedStatement.setDouble(18, orderData.getDiscount());
                    preparedStatement.setBoolean(19, orderData.getIsValid());
                    preparedStatement.setTimestamp(20, currentTime);
                    preparedStatement.setString(21, SYSTEM_USER);
                    preparedStatement.setTimestamp(22, currentTime);
                    preparedStatement.setString(23, SYSTEM_USER);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        });
        session.flush();
    }
}
