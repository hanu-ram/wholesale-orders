
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

import static com.levi.common.constant.Constants.SYSTEM_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleLineEntryDaoTest {

    @InjectMocks
    private ScheduleLineEntryDao scheduleLineEntryDao;

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
    void TestSaveWithQuery() throws SQLException {
        OrderData orderData = getOrderData();
        List<OrderData> orderDataList = new ArrayList<>();
        orderDataList.add(orderData);
        String expectedSchLineId = "test-sales-document-number"
                + "-" + "test-line-item"
                + "-" + "test-sch-line-item"
                + "-" + "test-material-code";

        String expectedLineEntryId = "test-sales-document-number"
                + "-" + "test-line-item"
                + "-" + "test-material-code";

        when(entityManager.unwrap(any())).thenReturn(session);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        when(entityManager.unwrap(any())).thenReturn(session);

        scheduleLineEntryDao.saveWithQuery(orderDataList);

        verify(session).doWork(workArgumentCaptor.capture());
        workArgumentCaptor.getValue().execute(connection);
        verifySetParameter(preparedStatement, orderData, expectedSchLineId, expectedLineEntryId);
        verify(session).flush();
    }

    private static void verifySetParameter(PreparedStatement preparedStatement, OrderData orderData, String schLineId, String lineId) throws SQLException {

        verify(preparedStatement).setString(1, schLineId);
        verify(preparedStatement).setString(2, lineId);
        verify(preparedStatement).setString(3, orderData.getSalesDocumentNumber());
        verify(preparedStatement).setString(4, orderData.getLineItem());
        verify(preparedStatement).setString(5, orderData.getSchLineItem());
        verify(preparedStatement).setString(6, orderData.getMaterialCode());
        verify(preparedStatement).setDouble(7, orderData.getOrderedQuantity());
        verify(preparedStatement).setDouble(8, orderData.getConfirmedQty());
        verify(preparedStatement).setDouble(9, orderData.getShippedQuantity());
        verify(preparedStatement).setString(10, orderData.getMaterialSize());
        verify(preparedStatement).setDate(11, orderData.getDeliveryDate());
        verify(preparedStatement).setDate(12, orderData.getCancelDate());
        verify(preparedStatement).setDate(13, orderData.getRequestedDeliveryDate());
        verify(preparedStatement).setDate(14, orderData.getApproxDueDate());
        verify(preparedStatement).setString(15, orderData.getRejectionReason());
        verify(preparedStatement).setDouble(16, orderData.getCancelledSkuQty());
        verify(preparedStatement).setString(17, orderData.getUom());
        verify(preparedStatement).setString(18, orderData.getShipTo());
        verify(preparedStatement).setString(19, orderData.getStoreName());
        verify(preparedStatement).setDouble(20, orderData.getPacUnconfirmedQuantity());
        verify(preparedStatement).setDouble(21, orderData.getVirUnconfirmedQuantity());
        verify(preparedStatement).setString(22, orderData.getScheduleStatus());
        verify(preparedStatement).setDate(23, orderData.getShipDate());
        verify(preparedStatement).setString(24, orderData.getInvoiceDoc());
        verify(preparedStatement).setDate(25, orderData.getInvoiceDate());
        verify(preparedStatement).setString(26, orderData.getRejectionReasonDescription());
        verify(preparedStatement).setDouble(27, orderData.getOpenQty());
        verify(preparedStatement).setDouble(28, orderData.getFixedQty());
        verify(preparedStatement).setBoolean(29, orderData.getDelInd());
        verify(preparedStatement, times(2)).setTimestamp(anyInt(), any(Timestamp.class));
        verify(preparedStatement).setString(31, SYSTEM_USER);
        verify(preparedStatement).setString(33, SYSTEM_USER);

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
