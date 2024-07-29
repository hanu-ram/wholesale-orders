package com.levi.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.util.OrderLoaderUtility;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.levi.common.constant.Constants.OC_ERR_TOPIC_ALERT;

@Service
@Slf4j
public class RetryService {

    @Autowired
    private OrderLoaderUtility orderLoaderUtility;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.retry.topic}")
    private String retryTopic;
    @Value("${kafka.error.topic}")
    private String errorTopic;
    @Value("${order.max.retry}")
    private Integer maxRetry;

    @Autowired
    @Qualifier("kafkaRetryProducer")
    private KafkaProducer<String, String> kafkaProducer;

    public void sendToRetryTopic(String key, String message) {
        sendToTopic(key, message, retryTopic);
    }

    private void sendToTopic(String key, String message, String topicName) {
        kafkaProducer.send(new ProducerRecord<>(topicName, key, message),
                (res, err) -> {
                    if (err == null) {
                        log.info("Message Published to kafka Topic {} with offset {}, partition {}, timestamp {}, partitionKey {}",
                                res.topic(), res.offset(),
                                res.partition(), res.timestamp(), key);
                    } else {
                        log.error("Failed to publish payload: {} with partitionKey: {}, error-message: {} ", message, key, err.getMessage());
                    }
                });
    }

    public void sendToErrorTopic(String key, String message) {
        sendToTopic(key, message, errorTopic);
    }

    public void prepareRetryListAndSendToRetryTopic(ConsumerRecords<String, String> records) throws JsonProcessingException {
        log.info("Preparing the list to send to retry/error kafka topic : {}", records.count());
        List<OrderData> orderDataList = new ArrayList<>();
        for (ConsumerRecord<String, String> rec : records) {
            OrderData orderData = orderLoaderUtility.getOrderDataFromJsonString(rec.value());
            orderDataList.add(orderData);
        }
        resendDataToKafkaTopic(orderDataList);
    }

    public void resendDataToKafkaTopic(List<OrderData> orderDataList) throws JsonProcessingException {
        log.info("Sending data to kafka topic.");
        boolean isErrorAlertAdded = false;
        for (OrderData orderData : orderDataList) {
            String salesDocumentNumber = orderData.getSalesDocumentNumber();
            if (orderData.getRetryCount() > maxRetry) {
                log.info("Retry count more than specified value sending to error topic order id : {}", salesDocumentNumber);
                String json = objectMapper.writeValueAsString(orderData);
                sendToErrorTopic(orderData.getSalesDocumentNumber(), json);
                if (!isErrorAlertAdded) {
                    isErrorAlertAdded = true;
                    log.error(OC_ERR_TOPIC_ALERT + ": Max retry attempt {} reached ", orderData.getRetryCount());
                }
                log.info(" message sent to error topic is : {}", json);
            } else {
                log.info("Retry attempt number : {} ", orderData.getRetryCount() + 1);
                log.info("Sending message to wholesale orders retry topic with key : {} ", salesDocumentNumber);
                orderData.setRetryCount(orderData.getRetryCount() + 1);
                String json = objectMapper.writeValueAsString(orderData);
                sendToRetryTopic(salesDocumentNumber, json);
                log.info("Sent to wholesale retry message topic message : {} ", json);
            }
        }
    }

}
