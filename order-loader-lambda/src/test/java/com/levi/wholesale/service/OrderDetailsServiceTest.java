package com.levi.wholesale.service;


import com.levi.wholesale.lambda.common.config.ConnectionManager;
import com.levi.wholesale.lambda.common.domain.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.domain.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.ScheduleLineEntryDao;
import com.levi.wholesale.lambda.common.dto.OrderData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;

import static org.mockito.Mockito.verify;

class OrderDetailsServiceTest {

    private static MockedStatic<ConnectionManager> configurationMockedStatic;

    private final OrderDetailsDao orderDetailsDao = Mockito.mock(OrderDetailsDao.class);

    private final LineEntryDao lineEntryDao = Mockito.mock(LineEntryDao.class);

    private final ScheduleLineEntryDao scheduleLineEntryDao = Mockito.mock(ScheduleLineEntryDao.class);

    @AfterEach
    public void afterMethod() {
        configurationMockedStatic.close();
    }

    @Test
    public void testSaveOrderDetails() throws Exception {
        configurationMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        Connection mockedConnection = Mockito.mock(Connection.class);
        configurationMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        OrderDetailsService orderDetailsService = new OrderDetailsService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);
        OrderData orderData = new OrderData();

        orderDetailsService.saveOrderDetails(orderData, mockedConnection);

        verify(orderDetailsDao).saveOrder(mockedConnection, orderData);
        verify(lineEntryDao).saveLineEntry(mockedConnection, orderData);
    }

    @Test()
    public void testSaveOrderDetails_ShouldThrowException_WhenSaveOrderThrowsEx() throws Exception {
        configurationMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        Connection mockedConnection = Mockito.mock(Connection.class);
        configurationMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);

        Mockito.doThrow(DataProcessingException.class).when(orderDetailsDao)
                .saveOrder(Mockito.eq(mockedConnection), Mockito.any(OrderData.class));
        OrderDetailsService orderDetailsService = new OrderDetailsService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);
        OrderData orderData = new OrderData();

        DataProcessingException dataProcessingException = Assertions.assertThrows(DataProcessingException.class,
                () -> orderDetailsService.saveOrderDetails(orderData, mockedConnection));

        Assertions.assertEquals(DataProcessingException.class, dataProcessingException.getClass());
    }

    @Test
    public void testSaveOrderDetails_ShouldAlsoInsertRecordInSchLineEntryTable() throws Exception {
        configurationMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        Connection mockedConnection = Mockito.mock(Connection.class);
        configurationMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        OrderDetailsService orderDetailsService = new OrderDetailsService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);
        OrderData orderData = new OrderData();
        orderData.setSalesDocumentNumber("OrderId");
        orderData.setLineItem("LineItem");
        orderData.setSchLineItem("TestSchLineItem");
        orderData.setMaterialName("TestMaterial");

        orderDetailsService.saveOrderDetails(orderData, mockedConnection);

        verify(orderDetailsDao).saveOrder(mockedConnection, orderData);
        verify(lineEntryDao).saveLineEntry(mockedConnection, orderData);
        verify(scheduleLineEntryDao).saveSchLineEntry(mockedConnection, orderData);
    }

}