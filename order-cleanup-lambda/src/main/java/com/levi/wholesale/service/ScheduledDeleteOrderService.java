package com.levi.wholesale.service;

import com.levi.wholesale.config.Configuration;
import com.levi.wholesale.lambda.common.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.dao.ScheduleLineEntryDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduledDeleteOrderService {

    private final OrderDetailsDao orderDetailsDao;

    private final LineEntryDao lineEntryDao;

    private final ScheduleLineEntryDao scheduleLineEntryDao;

    public ScheduledDeleteOrderService(OrderDetailsDao orderDetailsDao, LineEntryDao lineEntryDao,
                                       ScheduleLineEntryDao scheduleLineEntryDao) {
        this.orderDetailsDao = orderDetailsDao;
        this.lineEntryDao = lineEntryDao;
        this.scheduleLineEntryDao = scheduleLineEntryDao;
    }

    public String deleteData() throws InterruptedException {
        String threshold = Configuration.getRetentionPeriod();

        if (threshold == null || threshold.equals("")) {
            threshold = "730"; //days for 2 years
            log.info("Env variable is not set, so the default value is set to threshold value {}", threshold);
        }
        int rp = getIntValue(threshold, 0);

        //if retention period is set to 0 then no data will be deleted.
        if (rp != 0) {
            try {
                log.info("START deletion for thread  {} ", Thread.currentThread().getName());
                scheduleLineEntryDao.deleteSchLineEntry(rp);
                log.info("Schedule line entry data got deleted and current thread will sleep for {} ms", 10000);
                Thread.sleep(10000);
                log.info("line entry data will be deleted after delay for {} ms", 10000);
                lineEntryDao.deleteEntries(rp);
                orderDetailsDao.deleteOrders(rp);

                log.info(" Deletion Done ..");
                return "deleted";
            } catch (InterruptedException ie) {
                log.error(" Some error occurred while deleting the data with message {}, \n {}",
                        ie.getLocalizedMessage(), ie);
                Thread.currentThread().interrupt();
            } catch (Exception ie) {
                log.error(" Some error occurred while deleting the data with message {}, \n {}",
                        ie.getLocalizedMessage(), ie);
                return "failed";
            }
        }
        return "failed";
    }

    public int getIntValue(String str, int defaultVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ne) {
            return defaultVal;
        }
    }
}
