package com.levi.wholesale.handler;

import com.levi.wholesale.domain.OrderDetails;
import com.levi.wholesale.lambda.common.utils.CommonUtils;
import com.levi.wholesale.lambda.common.utils.FeedUtil;
import com.levi.wholesale.persistence.PersistenceManager;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Slf4j
public class OrderFeedProcessor {

    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
    private final PersistenceManager persistenceManager;
    public static final String FILE_PATTERN = "^salesorder_(.*?).csv";

    public OrderFeedProcessor(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public String processFeed(String feedFile, InputStream inputStream) {
        try {
            CommonUtils.validateFilePattern(FILE_PATTERN, feedFile);
            inputStream = FeedUtil.processBOM(inputStream);
            List<OrderDetails> orderDetailsList = FeedUtil.loadCSV(OrderDetails.class, inputStream);

            log.info("Feed details length {} ", orderDetailsList.size());
            List<Future<RecordMetadata>> futures = new ArrayList<>();
            for (OrderDetails orderDetails : orderDetailsList) {
                persistenceManager.addOrderDetailsToKafka(orderDetails, feedFile, futures);
            }

            if (futures.isEmpty()) {
                return SUCCESS;
            }
            waitForFutureToComplete(futures);
        } catch (IOException e) {
            log.error("IO Exception while removing BOM", e);
            return FAILURE;
        } catch (Exception ex) {
            log.error("Error while processing the Order feed" + ex);
            return FAILURE;
        }
        return SUCCESS;
    }

    private void waitForFutureToComplete(List<Future<RecordMetadata>> futures) {
        boolean done = false;
        while (!done) {
            long doneCount = 0L;
            for (Future<RecordMetadata> future : futures) {
                if (future.isDone() || future.isCancelled()) {
                    ++doneCount;
                }
            }
            if ((int) doneCount == futures.size()) {
                done = true;
            }
        }
    }
}
