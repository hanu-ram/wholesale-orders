package com.levi.wholesale.lambda.common.config;

import com.levi.wholesale.lambda.common.exception.DatabaseConnectionException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
public final class ConnectionManager {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static int retryCount = 0;
    private static int retryInitialInterval = 5;

    private ConnectionManager() {
    }

    public static Connection getConnection() throws InterruptedException {
        String url = Configuration.getDBUrl();
        String userName = Configuration.getDBUserName();
        String password = Configuration.getDBPassword();
        String driverName = Configuration.getDriverName();

        Connection connection = null;

        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, userName, password);
            connection.setAutoCommit(false);
            log.info("Db connection is successful.");
        } catch (SQLException | ClassNotFoundException ex) {
            log.error("Failed to create the database connection." + ex);

            if (MAX_RETRY_ATTEMPTS <= retryCount) {
                retryCount++;
                log.error("Retrying it with retry attempt number : {} ", retryCount);
                retryInitialInterval = retryInitialInterval * retryCount;
                Thread.sleep(retryInitialInterval);
                getConnection();
            } else {
                log.error("Max retry attempts have been reached. Exiting.");
                throw new DatabaseConnectionException("Failed to connect to Database. ", ex);
            }
        }

        return connection;
    }
}
