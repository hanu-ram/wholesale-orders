package com.levi.wholesale.lambda.common.dao;

import com.levi.wholesale.lambda.common.config.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.levi.wholesale.lambda.common.constant.QueryConstants.DELETE_OLD_ENTRIES;

@Slf4j
public class LineEntryDao {

    /*
      To delete the old line entry records by filtered order ids.
    */
    public void deleteEntries(int retentionPeriod) {
        try (Connection connection = ConnectionManager.getConnection()) {
            log.info("Entry data deletion started .");
            DateTime startTime = new DateTime();
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_OLD_ENTRIES)) {
                preparedStatement.setString(1, "" + retentionPeriod + " days 1 min");
                preparedStatement.executeUpdate();

                connection.commit();
                Duration duration = new Duration(startTime, new DateTime());
                log.info(" Entry data deleted successfully in {} ms", duration.getMillis());
            } catch (SQLException e) {
                log.error("Failed to delete records from line_entry table with message as : {} \n {}", e.getLocalizedMessage(), e);
            }
        } catch (Exception ie) {
            log.error(" Some error occurred while deleting the entry data with message {}, \n {}",
                    ie.getLocalizedMessage(), ie);
        }
    }
}
