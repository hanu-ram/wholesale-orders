package com.levi.lambda.common.dao;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.levi.wholesale.lambda.common.config.ConnectionManager;
import com.levi.wholesale.lambda.common.dao.ScheduleLineEntryDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduleLineEntryDaoTest {

    private Connection connection = mock(Connection.class);
    private final int threshold = 10;
    private PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(ScheduleLineEntryDao.class);
    }

    /**
     * to validate prepared statement data when orderIds are empty.
     *
     * @throws Exception
     */
    @Test
    void validatePSDataWithoutOrderIds_deleteEntriesByOrderIDs() throws Exception {
        ScheduleLineEntryDao  scheduleLineEntryDao = new ScheduleLineEntryDao();

        MockedStatic<ConnectionManager> connectionManagerMockedStatic = mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(connection);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        scheduleLineEntryDao.deleteSchLineEntry(threshold);

        verify(preparedStatement, times(1)).setString(1, "10 days 1 min");

        connectionManagerMockedStatic.close();
    }

    @Test
    void testDeleteSchLineEntry_throwsSQLException() throws SQLException {

        ScheduleLineEntryDao scheduleLineEntryDao = new ScheduleLineEntryDao();
        MockedStatic<ConnectionManager> connectionManagerMockedStatic = mockStatic(ConnectionManager.class);
        Connection mockedConnection = Mockito.mock(Connection.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenReturn(mockedConnection);

        when(mockedConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(SQLException.class);

        scheduleLineEntryDao.deleteSchLineEntry(threshold);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("Failed to delete records from schedule_line_entry"));

        connectionManagerMockedStatic.close();
    }

    @Test
    void testDeleteSchLineEntry_throwsException() {

        ScheduleLineEntryDao scheduleLineEntryDao = new ScheduleLineEntryDao();
        MockedStatic<ConnectionManager> connectionManagerMockedStatic = mockStatic(ConnectionManager.class);
        connectionManagerMockedStatic.when(ConnectionManager::getConnection).thenThrow(RuntimeException.class);

        scheduleLineEntryDao.deleteSchLineEntry(threshold);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith(" Some error occurred while deleting the Schedule line entry data"));
        connectionManagerMockedStatic.close();
    }


    public static ListAppender<ILoggingEvent> getListAppenderForClass(Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        ListAppender<ILoggingEvent> loggingEventListAppender = new ListAppender<>();
        loggingEventListAppender.start();
        logger.addAppender(loggingEventListAppender);
        return loggingEventListAppender;
    }
}