package com.levi.wholesale.delta.handler;

import com.levi.wholesale.lambda.common.handler.AbstractItemFileHandler;

import com.levi.wholesale.lambda.common.utils.CommonUtils;
import com.levi.wholesale.delta.persistence.PersistenceManager;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class OrderFeedHandler extends AbstractItemFileHandler {
    public static final String FILE_PATTERN = "^wholesale_orderdata_(.*?).csv";
    private String processStatus = null;

    @Override
    public String handleS3event(String fileName, InputStream inputStream, long startTime) {
        long t3 = System.currentTimeMillis();
        OrderFeedProcessor feedProcessor = new OrderFeedProcessor(new PersistenceManager());
        if (CommonUtils.filePatternCheck(OrderFeedHandler.FILE_PATTERN, fileName)) {
            log.info("Processing file: {}", fileName);
            processStatus = feedProcessor.processFeed(fileName, inputStream);
            log.info("Event Handler successfully processed the records in: {} for file {} with status {} ",
                    t3 - startTime, fileName, processStatus);
        } else {
            log.error("Invalid file format, accepted file formats are {}", FILE_PATTERN);
        }
        return processStatus;
    }
}
