package com.levi.wholesale.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.wholesale.lambda.common.config.Configuration;
import com.levi.wholesale.lambda.common.config.ConnectionManager;
import com.levi.wholesale.lambda.common.domain.dao.ErrorDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.domain.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.ScheduleLineEntryDao;
import com.levi.wholesale.lambda.common.dto.OrderData;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OrderDetailsErrorServiceTest {

    private final Connection mockedConnection = Mockito.mock(Connection.class);

    private final OrderDetailsDao orderDetailsDao = Mockito.mock(OrderDetailsDao.class);

    private final LineEntryDao lineEntryDao = Mockito.mock(LineEntryDao.class);

    private final ScheduleLineEntryDao scheduleLineEntryDao = Mockito.mock(ScheduleLineEntryDao.class);

    private final ErrorDetailsDao errorDetailsDao = Mockito.mock(ErrorDetailsDao.class);

    private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

    private final ErrorService errorService = Mockito.mock(ErrorService.class);


    @Test
    public void testSaveOrderDetails() throws Exception {
        OrderDetailsRetryService orderDetailsRetryService = new OrderDetailsRetryService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao,
                errorDetailsDao, objectMapper, errorService);
        OrderData orderData = getValidOrderData();
        ArrayList<String> ids = new ArrayList<>();

        MockedStatic<ConnectionManager> connectionManagerMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        MockedStatic<Configuration> configurationMockedStatic = Mockito.mockStatic(Configuration.class);
        configurationMockedStatic.when(Configuration::getMaxRetryAttempts).thenReturn(3);
        when(objectMapper.readValue("{}", OrderData.class)).thenReturn(orderData);
        when(errorDetailsDao.getErrorDetails(mockedConnection, orderData.getSalesDocumentNumber(), false))
                .thenReturn(ids);

        orderDetailsRetryService.saveOrderDetails("{}", mockedConnection, 0);

        verify(orderDetailsDao).saveOrder(mockedConnection, orderData, true);
        verify(lineEntryDao).saveLineEntry(mockedConnection, orderData, getLineEntryId(orderData));
        verify(scheduleLineEntryDao).saveSchLineEntry(mockedConnection,orderData, getErrorAndSkuId(orderData));
        connectionManagerMockedStatic.close();
        configurationMockedStatic.close();
    }

    @Test
    public void testSaveOrderDetails_ShouldSendToErrorTopic_WhenSaveOrderThrowsEx() throws Exception {
        OrderDetailsRetryService orderDetailsRetryService = new OrderDetailsRetryService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao,
                errorDetailsDao, objectMapper, errorService);
        OrderData orderData = getValidOrderData();
        ArrayList<String> ids = new ArrayList<>();

        MockedStatic<ConnectionManager> connectionManagerMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        MockedStatic<Configuration> configurationMockedStatic = Mockito.mockStatic(Configuration.class);
        configurationMockedStatic.when(Configuration::getMaxRetryAttempts).thenReturn(3);
        when(objectMapper.readValue("{}", OrderData.class)).thenReturn(orderData);
        when(errorDetailsDao.getErrorDetails(mockedConnection, orderData.getSalesDocumentNumber(), false))
                .thenReturn(ids);
        Mockito.doThrow(SQLException.class).when(orderDetailsDao)
                .saveOrder(mockedConnection, orderData, true);

        orderDetailsRetryService.saveOrderDetails("{}", mockedConnection, 0);

        verify(errorService).sendToErrorTopic(anyString());
        connectionManagerMockedStatic.close();
        configurationMockedStatic.close();
    }

    @Test
    public void testSaveOrderDetails_shouldSaveErrorDetails() throws Exception {
        OrderDetailsRetryService orderDetailsRetryService = new OrderDetailsRetryService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao,
                errorDetailsDao, objectMapper, errorService);
        OrderData orderData = getInValidOrderData();
        ArrayList<String> ids = new ArrayList<>();
        String errorMessage = "Missing some of mandatory fields : " + "materialSize";

        MockedStatic<ConnectionManager> connectionManagerMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        when(objectMapper.readValue("{}", OrderData.class)).thenReturn(orderData);
        when(errorDetailsDao.getErrorDetails(mockedConnection, orderData.getSalesDocumentNumber(), false))
                .thenReturn(ids);

        orderDetailsRetryService.saveOrderDetails("{}", mockedConnection, 0);

        verify(orderDetailsDao).saveOrder(mockedConnection, orderData, false);
        verify(errorDetailsDao).saveErrorDetails(mockedConnection, orderData, getErrorAndSkuId(orderData), errorMessage, false);
        connectionManagerMockedStatic.close();
    }


    @Test
    public void testSaveOrderDetails_shouldSave_processedErrorDetails() throws Exception {
        OrderDetailsRetryService orderDetailsRetryService = new OrderDetailsRetryService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao,
                errorDetailsDao, objectMapper, errorService);
        OrderData orderData = getValidOrderData();
        String errorId = getErrorAndSkuId(orderData);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(errorId);

        MockedStatic<ConnectionManager> connectionManagerMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        when(objectMapper.readValue("{}", OrderData.class)).thenReturn(orderData);
        when(errorDetailsDao.getErrorDetails(mockedConnection, orderData.getSalesDocumentNumber(), false))
                .thenReturn(ids);

        orderDetailsRetryService.saveOrderDetails("{}", mockedConnection, 0);

        verify(errorDetailsDao).saveErrorDetails(mockedConnection, orderData, getErrorAndSkuId(orderData), "", true);
        verify(orderDetailsDao).saveOrder(mockedConnection, orderData, true);
        connectionManagerMockedStatic.close();
    }


    @Test
    public void saveOrderDetails_ShouldNotSaveData() throws JsonProcessingException {
        OrderDetailsRetryService orderDetailsRetryService = new OrderDetailsRetryService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao,
                errorDetailsDao, objectMapper, errorService);

        MockedStatic<ConnectionManager> connectionManagerMockedStatic = Mockito.mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);
        when(objectMapper.readValue("{}", OrderData.class)).thenThrow(JsonProcessingException.class);

        orderDetailsRetryService.saveOrderDetails("{}", mockedConnection, 0);

        verifyNoInteractions(orderDetailsDao);
        verifyNoInteractions(lineEntryDao);
        verifyNoInteractions(scheduleLineEntryDao);
        verifyNoInteractions(errorDetailsDao);

        connectionManagerMockedStatic.close();
    }


    private static OrderData getValidOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSoldTo("TestSoldTo");
        orderData.setPurchaseOrderNumber("TestPurchaseOrderNumber");
        orderData.setSalesDocumentNumber("TestSalesDocumentNumber");
        orderData.setCurrency("USD");
        orderData.setRegion("TestRegion");
        orderData.setCountryCode("TestCountryCode");
        orderData.setMaterialCode("TestMaterialCode");
        orderData.setLineItem("TestLineItem");
        orderData.setSchLineItem("TestSchLineItem");
        orderData.setMaterialSize("TestMaterialSize");
        return orderData;
    }
    private static OrderData getInValidOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSoldTo("TestSoldTo");
        orderData.setPurchaseOrderNumber("TestPurchaseOrderNumber");
        orderData.setSalesDocumentNumber("TestSalesDocumentNumber");
        orderData.setCurrency("USD");
        orderData.setRegion("TestRegion");
        orderData.setCountryCode("TestCountryCode");
        orderData.setMaterialCode("TestMaterialCode");
        orderData.setLineItem("TestLineItem");
        orderData.setSchLineItem("TestSchLineItem");
        orderData.setMaterialSize("");
        return orderData;
    }

    private static String getErrorAndSkuId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem() + "-" + orderData.getMaterialCode();
    }

    private static String getLineEntryId(OrderData orderData) {
        return orderData.getSalesDocumentNumber() + "-" + orderData.getLineItem()
                + "-" + orderData.getMaterialCode();
    }

}
