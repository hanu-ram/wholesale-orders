package com.levi.wholesale.service;

import com.levi.wholesale.config.Configuration;
import com.levi.wholesale.lambda.common.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.dao.ScheduleLineEntryDao;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ScheduledDeleteOrderServiceTest {

    private final OrderDetailsDao orderDetailsDao = Mockito.mock(OrderDetailsDao.class);

    private final LineEntryDao lineEntryDao = Mockito.mock(LineEntryDao.class);

    private final ScheduleLineEntryDao scheduleLineEntryDao = Mockito.mock(ScheduleLineEntryDao.class);


    @Test
    void verifyIfEnvVariableDataFetched_deleteData() throws InterruptedException {
        ScheduledDeleteOrderService scheduledDeleteOrderService = new ScheduledDeleteOrderService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);

        try (MockedStatic<Configuration> mockedStaticConfiguration = Mockito.mockStatic(Configuration.class)) {
            mockedStaticConfiguration.when(Configuration::getRetentionPeriod).thenReturn("100");

            String actual = scheduledDeleteOrderService.deleteData();

            assertEquals("deleted", actual);
            verify(orderDetailsDao).deleteOrders(100);
            verify(lineEntryDao).deleteEntries(100);
            verify(scheduleLineEntryDao).deleteSchLineEntry(100);
        }
    }

    @Test
    void testDeleteData_setDefaultRetention_forNullThreshold() throws InterruptedException {
        ScheduledDeleteOrderService scheduledDeleteOrderService = new ScheduledDeleteOrderService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);

        try (MockedStatic<Configuration> mockedStaticConfiguration = Mockito.mockStatic(Configuration.class)) {
            mockedStaticConfiguration.when(Configuration::getRetentionPeriod).thenReturn(null);

            String actual = scheduledDeleteOrderService.deleteData();

            assertEquals("deleted", actual);
            verify(orderDetailsDao).deleteOrders(730);
            verify(lineEntryDao).deleteEntries(730);
            verify(scheduleLineEntryDao).deleteSchLineEntry(730);
        }
    }

    @Test
    void testDeleteData_setDefaultRetention_forEmptyThreshold() throws InterruptedException {
        ScheduledDeleteOrderService scheduledDeleteOrderService = new ScheduledDeleteOrderService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);

        try (MockedStatic<Configuration> mockedStaticConfiguration = Mockito.mockStatic(Configuration.class)) {
            mockedStaticConfiguration.when(Configuration::getRetentionPeriod).thenReturn("");

            String actual = scheduledDeleteOrderService.deleteData();

            assertEquals("deleted", actual);
            verify(orderDetailsDao).deleteOrders(730);
            verify(lineEntryDao).deleteEntries(730);
            verify(scheduleLineEntryDao).deleteSchLineEntry(730);
        }
    }

    @Test
    void testDeleteData_returnsFailed_onException() throws InterruptedException {
        ScheduledDeleteOrderService scheduledDeleteOrderService = new ScheduledDeleteOrderService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);

        try (MockedStatic<Configuration> mockedStaticConfiguration = Mockito.mockStatic(Configuration.class)) {
            mockedStaticConfiguration.when(Configuration::getRetentionPeriod).thenReturn("100");
            doThrow(RuntimeException.class).when(scheduleLineEntryDao).deleteSchLineEntry(100);

            String actual = scheduledDeleteOrderService.deleteData();

            assertEquals("failed", actual);
        }
    }

    @Test
    void testDeleteData_returnsFailed_onZeroRetentionPeriod() throws InterruptedException {
        ScheduledDeleteOrderService scheduledDeleteOrderService = new ScheduledDeleteOrderService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);

        try (MockedStatic<Configuration> mockedStaticConfiguration = Mockito.mockStatic(Configuration.class)) {
            mockedStaticConfiguration.when(Configuration::getRetentionPeriod).thenReturn("0");
            String actual = scheduledDeleteOrderService.deleteData();
            assertEquals("failed", actual);
            verifyNoInteractions(orderDetailsDao);
            verifyNoInteractions(lineEntryDao);
            verifyNoInteractions(scheduleLineEntryDao);
        }
    }

    @Test
    void testDeleteData_returnsFailed_onNumberFormatException() throws InterruptedException {
        ScheduledDeleteOrderService scheduledDeleteOrderService = new ScheduledDeleteOrderService(orderDetailsDao, lineEntryDao, scheduleLineEntryDao);

        try (MockedStatic<Configuration> mockedStaticConfiguration = Mockito.mockStatic(Configuration.class)) {
            mockedStaticConfiguration.when(Configuration::getRetentionPeriod).thenReturn("abc");

            String actual = scheduledDeleteOrderService.deleteData();

            assertEquals("failed", actual);
            verifyNoInteractions(orderDetailsDao);
            verifyNoInteractions(lineEntryDao);
            verifyNoInteractions(scheduleLineEntryDao);
        }
    }
}
