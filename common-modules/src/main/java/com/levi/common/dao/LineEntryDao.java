package com.levi.common.dao;

import com.levi.common.constant.QueryConstants;
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

@Slf4j
@Repository
public class LineEntryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveWithQuery(List<OrderData> orderDataList) {
        Timestamp currentTime = Timestamp.valueOf(CommonUtils.getUtcDateTime());

        try (Session session = entityManager.unwrap(Session.class)) {
            session.doWork(connection -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(QueryConstants.LINE_ENTRY_UPSERT)) {

                    for (OrderData orderData : orderDataList) {
                        preparedStatement.setString(1, getLineEntryId(orderData));
                        preparedStatement.setString(2, orderData.getSalesDocumentNumber());
                        preparedStatement.setString(3, orderData.getMaterialCode());
                        preparedStatement.setString(4, orderData.getLineItem());
                        preparedStatement.setString(5, orderData.getItemCategory());
                        preparedStatement.setString(6, orderData.getConsumerGroup());
                        preparedStatement.setString(7, orderData.getMaterialName());
                        preparedStatement.setString(8, orderData.getPlant());
                        preparedStatement.setString(9, orderData.getStockType());
                        preparedStatement.setString(10, orderData.getBrand());
                        preparedStatement.setString(11, orderData.getCurrency());
                        preparedStatement.setDouble(12, orderData.getWholesalePrice());
                        preparedStatement.setDate(13, orderData.getWholesalePriceValidFrom());
                        preparedStatement.setDate(14, orderData.getWholesalePriceValidTo());
                        preparedStatement.setDouble(15, orderData.getDiscounts());
                        preparedStatement.setDouble(16, orderData.getGrossValue());
                        preparedStatement.setDouble(17, orderData.getExpectedPriceEdi());
                        preparedStatement.setDouble(18, orderData.getLeviRetailPrice());
                        preparedStatement.setDouble(19, orderData.getCustomerExpcMsrp());
                        preparedStatement.setDouble(20, orderData.getRpmPrice());
                        preparedStatement.setDouble(21, orderData.getNetValue());
                        preparedStatement.setDouble(22, orderData.getNetPrice());
                        preparedStatement.setDouble(23, orderData.getQuantity());
                        preparedStatement.setString(24, orderData.getDescription());
                        preparedStatement.setDouble(25, orderData.getSubtotal());
                        preparedStatement.setString(26, orderData.getLineItemStatus());
                        preparedStatement.setDouble(27, orderData.getRejectedQty());
                        preparedStatement.setTimestamp(28, currentTime);
                        preparedStatement.setString(29, SYSTEM_USER);
                        preparedStatement.setTimestamp(30, currentTime);
                        preparedStatement.setString(31, SYSTEM_USER);
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                }
            });
            session.flush();
        }
    }

    private static String getLineEntryId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem() + "-" + orderData.getMaterialCode();
    }
}
