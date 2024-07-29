
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDetailsDaoTest {

    @InjectMocks
    private OrderDetailsDao orderDetailsDao;
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

        when(entityManager.unwrap(any())).thenReturn(session);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        orderDetailsDao.saveWithQuery(orderDataList);

        verify(session).doWork(workArgumentCaptor.capture());
        workArgumentCaptor.getValue().execute(connection);
        verifySetParameter(preparedStatement, orderData, true);
        verify(session).flush();
    }

    private static void verifySetParameter(PreparedStatement preparedStatement, OrderData orderData, boolean status) throws SQLException {
        verify(preparedStatement, atLeastOnce()).setString(1, orderData.getSalesDocumentNumber());
        verify(preparedStatement, atLeastOnce()).setString(2, orderData.getSalesOrganization());
        verify(preparedStatement, atLeastOnce()).setString(3, orderData.getSoldTo());
        verify(preparedStatement, atLeastOnce()).setString(4, orderData.getSalesDocumentType());
        verify(preparedStatement, atLeastOnce()).setDate(5, orderData.getSalesDocumentDate());
        verify(preparedStatement, atLeastOnce()).setString(6, orderData.getPoType());
        verify(preparedStatement, atLeastOnce()).setString(7, orderData.getPurchaseOrderNumber());
        verify(preparedStatement, atLeastOnce()).setString(8, orderData.getPlanningGroup());
        verify(preparedStatement, atLeastOnce()).setString(9, orderData.getOrderReason());
        verify(preparedStatement, atLeastOnce()).setString(10, orderData.getRegion());
        verify(preparedStatement, atLeastOnce()).setString(11, orderData.getCountryCode());
        verify(preparedStatement, atLeastOnce()).setString(12, orderData.getCustomerName());
        verify(preparedStatement, atLeastOnce()).setString(13, orderData.getStatus());
        verify(preparedStatement, atLeastOnce()).setDouble(14, orderData.getTax());
        verify(preparedStatement, atLeastOnce()).setDouble(15, orderData.getAmount());
        verify(preparedStatement, atLeastOnce()).setString(16, orderData.getInvoiceNumber());
        verify(preparedStatement, atLeastOnce()).setString(17, orderData.getTrackingNumber());
        verify(preparedStatement, atLeastOnce()).setDouble(18, orderData.getDiscount());
        verify(preparedStatement, atLeastOnce()).setBoolean(19, orderData.getIsValid());
        verify(preparedStatement, times(2)).setTimestamp(anyInt(), any(Timestamp.class));
        verify(preparedStatement).setString(21, "SYSTEM");
        verify(preparedStatement).setString(23, "SYSTEM");
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
