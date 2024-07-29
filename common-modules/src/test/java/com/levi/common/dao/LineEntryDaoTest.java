
package com.levi.common.dao;

import com.levi.wholesale.common.dto.OrderData;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineEntryDaoTest {
    @InjectMocks
    private LineEntryDao lineEntryDao;

    @Mock
    private EntityManager entityManager;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private Session session;

    @Captor
    private ArgumentCaptor<Work> workArgumentCaptor;

    @Test
    void testSaveWithQuery() throws SQLException {
        OrderData orderData = getOrderData();
        List<OrderData> orderDataList = new ArrayList<>();
        orderDataList.add(orderData);
        String expectedId = "test-sales-document-number-test-line-item-test-material-code";

        when(entityManager.unwrap(any())).thenReturn(session);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        lineEntryDao.saveWithQuery(orderDataList);

        verify(session).doWork(workArgumentCaptor.capture());
        workArgumentCaptor.getValue().execute(connection);
        verifySetString(preparedStatement, orderData, expectedId);
        verify(session).flush();
    }

    private static void verifySetString(PreparedStatement preparedStatement, OrderData orderData, String id) throws SQLException {

        verify(preparedStatement).setString(1, id);
        verify(preparedStatement).setString(2, orderData.getSalesDocumentNumber());
        verify(preparedStatement).setString(3, orderData.getMaterialCode());
        verify(preparedStatement).setString(4, orderData.getLineItem());
        verify(preparedStatement).setString(5, orderData.getItemCategory());
        verify(preparedStatement).setString(6, orderData.getConsumerGroup());
        verify(preparedStatement).setString(7, orderData.getMaterialName());
        verify(preparedStatement).setString(8, orderData.getPlant());
        verify(preparedStatement).setString(9, orderData.getStockType());
        verify(preparedStatement).setString(10, orderData.getBrand());
        verify(preparedStatement).setString(11, orderData.getCurrency());
        verify(preparedStatement).setDouble(12, orderData.getWholesalePrice());
        verify(preparedStatement).setDate(13, orderData.getWholesalePriceValidFrom());
        verify(preparedStatement).setDate(14, orderData.getWholesalePriceValidTo());
        verify(preparedStatement).setDouble(15, orderData.getDiscounts());
        verify(preparedStatement).setDouble(16, orderData.getGrossValue());
        verify(preparedStatement).setDouble(17, orderData.getExpectedPriceEdi());
        verify(preparedStatement).setDouble(18, orderData.getLeviRetailPrice());
        verify(preparedStatement).setDouble(19, orderData.getCustomerExpcMsrp());
        verify(preparedStatement).setDouble(20, orderData.getRpmPrice());
        verify(preparedStatement).setDouble(21, orderData.getNetValue());
        verify(preparedStatement).setDouble(22, orderData.getNetPrice());
        verify(preparedStatement).setDouble(23, orderData.getQuantity());
        verify(preparedStatement).setString(24, orderData.getDescription());
        verify(preparedStatement).setDouble(25, orderData.getSubtotal());
        verify(preparedStatement).setString(26, orderData.getLineItemStatus());
        verify(preparedStatement).setDouble(27, orderData.getRejectedQty());
        verify(preparedStatement, times(2)).setTimestamp(anyInt(), any(Timestamp.class));
        verify(preparedStatement).setString(29, "SYSTEM");
        verify(preparedStatement).setString(31, "SYSTEM");
        verify(preparedStatement).addBatch();

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
