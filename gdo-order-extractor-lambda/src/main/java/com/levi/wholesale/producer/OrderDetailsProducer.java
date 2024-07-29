package com.levi.wholesale.producer;

import com.levi.wholesale.config.ObjectMapperConfig;
import com.levi.wholesale.lambda.common.config.Configuration;
import com.levi.wholesale.lambda.common.domain.ErrorEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import static com.levi.wholesale.lambda.common.constant.Constants.EX_ERR_TOPIC_ALERT;

@Slf4j
@NoArgsConstructor
public class OrderDetailsProducer {

    private static final String MODULE_NAME = "ORDER-EXTRACTOR-LAMBDA";
    private static final String EVENT_NAME = "INVALID_MESSAGE";

    public void publishOrderDetailsToKafka(Producer<String, String> producer,
                                           String payload, String partitionKey, List<Future<RecordMetadata>> futuresList) throws IOException {
        try {
            String storeKafkaTopic = Configuration.getKafkaTopic();
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(storeKafkaTopic, partitionKey, payload);
            Future<RecordMetadata> future = producer.send(producerRecord, (recordMetadata, e) -> {
                if (e == null) {
                    log.info("Message Published to kafka Topic {} with offset {}, partition {}, timestamp {}, partitionKey {}",
                            recordMetadata.topic(), recordMetadata.offset(),
                            recordMetadata.partition(), recordMetadata.timestamp(), partitionKey);
                }
            });
            futuresList.add(future);
        } catch (Exception ex) {
            log.error(EX_ERR_TOPIC_ALERT + ": Failed to publish Message {} with partitionKey -{}", payload, partitionKey);
            postErrorToKafkaTopic(producer, payload, ex.getMessage(), EVENT_NAME, MODULE_NAME);
        }
    }

    public void postErrorToKafkaTopic(Producer<String, String> producer,
                                      String payload, String errorMsg, String eventName, String module) throws IOException {

        String eventId = UUID.randomUUID().toString();
        ErrorEvent errorEvent = new ErrorEvent();
        errorEvent
                .withEventId(eventId)
                .withEventModule(module)
                .withErrorMessage(errorMsg)
                .withEventName(eventName)
                .withPayload(payload);

        String errorKafkaTopic = Configuration.getKafkaErrorTopic();

        String errorPayload = ObjectMapperConfig.getObjectMapper().writeValueAsString(errorEvent);
        ProducerRecord<String, String> errorRecord = new ProducerRecord<>(errorKafkaTopic, eventId, errorPayload);

        producer.send(errorRecord, (recordMetadata, e) -> {
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
