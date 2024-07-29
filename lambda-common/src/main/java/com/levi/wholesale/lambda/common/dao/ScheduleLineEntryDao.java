package com.levi.wholesale.lambda.common.dao;

import com.levi.wholesale.lambda.common.config.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.levi.wholesale.lambda.common.constant.QueryConstants.DELETE_OLD_SCH_LINE_ENTRY_DATA;

@Slf4j
public class ScheduleLineEntryDao {

    /*
      To delete the old line entry records by filtered order ids.
    */
    public void deleteSchLineEntry(int retentionPeriod) {
        try (Connection connection = ConnectionManager.getConnection()) {
            DateTime startTime = new DateTime();
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_OLD_SCH_LINE_ENTRY_DATA)) {
                preparedStatement.setString(1, "" + retentionPeriod + " days 1 min");
                preparedStatement.executeUpdate();

                connection.commit();
                Duration duration = new Duration(startTime, new DateTime());
                log.info("Schedule line entry entry data deleted successfully  in {} ms", duration.getMillis());
            } catch (SQLException e) {
                log.error("Failed to delete records from schedule_line_entry with message as : {} \n{}", e.getLocalizedMessage(), e);
            }
        } catch (Exception ie) {
            log.error(" Some error occurred while deleting the Schedule line entry data with message {}, \n {}",
                    ie.getLocalizedMessage(), ie);
        }
    }
}
