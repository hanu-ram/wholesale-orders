package com.levi.wholesale.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.common.model.ErrorEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
@Slf4j
public class CxKafkaProducer {

    @Value("${kafka.error.topic}")
    private String errorTopic;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String MODULE_NAME = "CX-FEED";
    private static final String EVENT_NAME = "INVALID_MESSAGE";

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    public void publishOrderDetailsToKafka(String payload, String partitionKey, String topicName) throws IOException {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, partitionKey, payload);
        try {
            kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                if (e == null) {
                    log.info("Message Published to kafka Topic {} with offset {}, partition {}, timestamp {}, partitionKey {}",
                            recordMetadata.topic(), recordMetadata.offset(),
                            recordMetadata.partition(), recordMetadata.timestamp(), partitionKey);
                }
            });
        } catch (Exception ex) {
            log.error("Failed to publish Message {} with partitionKey -{}", payload, partitionKey);
            postErrorToKafkaTopic(payload, ex.getMessage(), EVENT_NAME, MODULE_NAME);
        }
    }

    private void postErrorToKafkaTopic(String payload, String errorMsg, String eventName, String module) throws JsonProcessingException {

        String eventId = UUID.randomUUID().toString();
        ErrorEvent errorEvent = new ErrorEvent();
        errorEvent
                .withEventId(eventId)
                .withEventModule(module)
                .withErrorMessage(errorMsg)
                .withEventName(eventName)
                .withPayload(payload);

        String errorPayload = objectMapper.writeValueAsString(errorEvent);

        ProducerRecord<String, String> errorRecord = new ProducerRecord<>(errorTopic, eventId, errorPayload);

        kafkaProducer.send(errorRecord, (recordMetadata, e) -> {
            if (e == null) {
                log.info("Message Published to Error kafka Topic {} with offset {}, partition {}, timestamp {}",
                        recordMetadata.topic(), recordMetadata.offset(),
                        recordMetadata.partition(), recordMetadata.timestamp());
            } else {
                log.error("Failed to publish Message {}", errorPayload);
                throw new KafkaException("Failed to publish Message");
            }
        });
    }
}
