package com.levi.order.consumer;

import com.levi.config.OrderLoaderConfig;
import com.levi.order.service.OrderProcessingService;
import com.levi.order.service.RetryService;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.levi.common.constant.Constants.OC_PROCESS_FAILURE_ALERT;

@Component
@Slf4j
public class RetryConsumer {
    @Autowired
    private OrderLoaderConfig orderLoaderConfig;
    @Autowired
    private OrderProcessingService orderProcessingService;
    @Autowired
    private RetryService retryService;

    @Value("${kafka.retry.topic}")
    private String retryTopicName;
    @Value("${kafka.poll.timeOut}")
    private Long timeOut;
    @Value("${retry.consumer.time.ms}")
    private Long retryTime;

    @Scheduled(cron = "${consumer.retry.cron}")
    public void retryConsumer() {
        long startTime = System.currentTimeMillis();
        log.info("Starting retry consumer...");
        KafkaConsumer<String, String> retryKafkaConsumer = orderLoaderConfig.getRetryKafkaConsumer();
        retryKafkaConsumer.subscribe(Collections.singleton(retryTopicName));

        do {
            try {
                ConsumerRecords<String, String> records = retryKafkaConsumer.poll(timeOut);
                List<OrderData> orderDataList = new ArrayList<>();
                boolean isValidData = false;

                for (ConsumerRecord<String, String> rec : records) {
                    isValidData = orderProcessingService.validateAndPopulateOrderList(rec.value(), orderDataList);
                    if (!isValidData) {
                        break;
                    }
                }
                if (records.count() > 0) {
                    if (!isValidData) {
                        retryService.prepareRetryListAndSendToRetryTopic(records);
                    } else {
                        orderProcessingService.saveOrderDataList(orderDataList);
                    }
                }
                retryKafkaConsumer.commitAsync();
            } catch (Exception ex) {
                log.error(OC_PROCESS_FAILURE_ALERT + " : Exception when consuming data from retry topic. ", ex);
            }
        } while (System.currentTimeMillis() < startTime + retryTime);
        log.info("Retry consumer terminated after {} ms.", retryTime);
    }

}
