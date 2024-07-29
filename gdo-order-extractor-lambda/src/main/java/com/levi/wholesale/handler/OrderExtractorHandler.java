package com.levi.wholesale.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.levi.wholesale.lambda.common.config.Configuration;
import com.levi.wholesale.persistence.PersistenceManager;
import com.levi.wholesale.producer.OrderDetailsProducer;
import com.levi.wholesale.util.FileDataExtractorUtilities;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.levi.wholesale.lambda.common.constant.Constants.EX_PROCESSING_FAILED;


@Slf4j
public class OrderExtractorHandler implements RequestHandler<ScheduledEvent, String> {

    private static final String PROCESS_STATUS_SUCCESS = "success";
    /*
        To run it locally only.It will not be used by any lambda.
     */
    public static void main(String[] args) {

        OrderExtractorHandler extractorHandler = new OrderExtractorHandler();
        extractorHandler.handleRequest(null, null);
    }

    @SneakyThrows
    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        String bucketName = Configuration.getGDOBucketName();
        String prefix = Configuration.getGDOBucketPrefixName();
        String destinationPrefix = Configuration.getDestinationPrefixName();
        OrderFeedProcessor feedProcessor = new OrderFeedProcessor(new PersistenceManager(new OrderDetailsProducer()));
        FileDataExtractorUtilities fileDataExtractorUtilities = new FileDataExtractorUtilities();
        Optional<List<String>> stringList =
                fileDataExtractorUtilities.getGoogleStorageObjectNamesByPrefixAndLatestUTCModifiedDate(
                        bucketName, prefix + "/", 1);
        List<String> fileNameList = new ArrayList<>();
        if (stringList.isPresent()) {
            fileNameList = stringList.get();
        }
        String processStatus = null;
        for (String fileName : fileNameList) {
            long startTime = System.currentTimeMillis();
            InputStream inputStream = fileDataExtractorUtilities.getGoogleStorageObjectInputStream(
                    bucketName, fileName);
            log.info("Processing file: {}", fileName);
            processStatus = feedProcessor.processFeed(fileName, inputStream);
            if (processStatus.equals(PROCESS_STATUS_SUCCESS)) {
                log.info("Moving the file {} to Archive folder under {}", fileName, destinationPrefix);
                fileDataExtractorUtilities.copyObject(bucketName, destinationPrefix + "/"
                        + Configuration.getArchiveFolderName() + "/", fileName);
            } else {
                log.info(EX_PROCESSING_FAILED + " : Moving the file {} to error folder under {}", fileName, destinationPrefix);
                fileDataExtractorUtilities.copyObject(bucketName, destinationPrefix + "/"
                        + Configuration.getErrorName() + "/", fileName);
            }
            fileDataExtractorUtilities.deleteObject(bucketName, fileName);
            long endTime = System.currentTimeMillis();
            log.info("Event Handler successfully processed the records in: {} for file {} with status {} ",
                    endTime - startTime, fileName, processStatus);
        }
        return processStatus;
    }
}
