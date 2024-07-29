package com.levi.wholesale.service;

import com.levi.wholesale.lambda.common.domain.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.domain.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.ScheduleLineEntryDao;
import com.levi.wholesale.lambda.common.dto.OrderData;
import com.levi.wholesale.lambda.common.exception.OrderProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class OrderDetailsService {

    private final OrderDetailsDao orderDetailsDao;

    private final LineEntryDao lineEntryDao;

    private final ScheduleLineEntryDao scheduleLineEntryDao;

    public OrderDetailsService(OrderDetailsDao orderDetailsDao, LineEntryDao lineEntryDao,
                               ScheduleLineEntryDao scheduleLineEntryDao) {
        this.orderDetailsDao = orderDetailsDao;
        this.lineEntryDao = lineEntryDao;
        this.scheduleLineEntryDao = scheduleLineEntryDao;
    }

    public void saveOrderDetails(OrderData orderData, Connection connection) {

        //Validate mandatoryFields

        log.info("Saving the order data for order id {}", orderData.getSalesDocumentNumber());

        try {
            log.debug("Inserting to order_details table {}", orderData.getSalesDocumentNumber());
            orderDetailsDao.saveOrder(connection, orderData);

            log.debug("Inserting to line_entry table {}", orderData.getSalesDocumentNumber());
            lineEntryDao.saveLineEntry(connection, orderData);

            if (shouldInsertSchLineEntryRecord(orderData)) {
                log.debug("Inserting to schedule_line_entry table {}", orderData.getSalesDocumentNumber());
                scheduleLineEntryDao.saveSchLineEntry(connection, orderData);
            }
            connection.commit();
        } catch (SQLException ex) {
            log.error("Error when saving wholesale order details ", ex);
            throw new OrderProcessingException("Unable to save order data ", ex);
        }
    }

    private static boolean shouldInsertSchLineEntryRecord(OrderData orderData) {
        return orderData.getSalesDocumentNumber() != null && orderData.getLineItem() != null
                && orderData.getSchLineItem() != null && orderData.getMaterialName() != null;
    }
}
