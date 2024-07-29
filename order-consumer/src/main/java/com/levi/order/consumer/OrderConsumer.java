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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.levi.common.constant.Constants.OC_PROCESS_FAILURE_ALERT;

@Slf4j
@Component
public class OrderConsumer {
    @Autowired
    private OrderProcessingService orderProcessingService;

    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${kafka.poll.timeOut}")
    private Integer timeOut;

    @Autowired
    private OrderLoaderConfig orderLoaderConfig;

    @Autowired
    private RetryService retryService;

    private Boolean runForever = true;

    public Runnable createKafkaConsumer() {
        return this::consumeOrder;
    }

    public void consumeOrder() {
        KafkaConsumer<String, String> kafkaConsumer = orderLoaderConfig.getKafkaConsumer();
        kafkaConsumer.subscribe(Collections.singleton(topicName));
        do {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(timeOut);
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
                    kafkaConsumer.commitSync();
                }
            } catch (Exception ex) {
                log.error(OC_PROCESS_FAILURE_ALERT + " : Exception when consuming data from order topic. ", ex);
            }
        } while (runForever);
    }

}
