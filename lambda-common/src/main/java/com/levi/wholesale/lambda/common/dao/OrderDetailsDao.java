package com.levi.wholesale.lambda.common.dao;

import com.levi.wholesale.lambda.common.config.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.levi.wholesale.lambda.common.constant.QueryConstants.DELETE_OLD_ORDERS;

@Slf4j
public class OrderDetailsDao {

    /*
      To get the old records by order type and order type wise retention period.
    */
    public void deleteOrders(int retentionPeriod) {
        DateTime startTime = new DateTime();
        try (Connection connection = ConnectionManager.getConnection()) {
            deleteFromOrderTable(retentionPeriod, startTime, connection);
        } catch (Exception ie) {
            log.error(" Some error occurred while deleting the order data with message {}, \n {}",
                    ie.getLocalizedMessage(), ie);
        }
    }

    private void deleteFromOrderTable(int retentionPeriod, DateTime startTime, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_OLD_ORDERS)) {
            preparedStatement.setString(1, "" + retentionPeriod + " days 1 min");
            preparedStatement.executeUpdate();

            connection.commit();
            Duration duration = new Duration(startTime, new DateTime());
            log.info(" ORDER data deleted successfully in {} ms", duration.getMillis());
        } catch (SQLException e) {
            log.error("Failed to delete records from order_details table with message as : {} \n {}", e.getLocalizedMessage(), e);
        }
    }
}
