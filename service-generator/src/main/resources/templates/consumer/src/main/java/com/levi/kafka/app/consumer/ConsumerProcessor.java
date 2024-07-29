package com.levi.kafka.app.consumer;

import com.levi.kafka.app.processor.MessageProcessor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

@Component
public class ConsumerProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerProcessor.class);
    @Value("${kafka.consumer.topic.name}")
    private String topicName;

    @Value("${kafka.consumer.pool.timeout}")
    private String pollTimeout;

    void processInLoop(Consumer<String, String> consumer, ExecutorService exec) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(Long.parseLong(pollTimeout)));
        if (records != null && !records.isEmpty()) {
            for (ConsumerRecord<String, String> stringConsumerRecord : records.records(this.topicName)) {
                long offset = stringConsumerRecord.offset();
                int topicPartiton = stringConsumerRecord.partition();
                String message = (String) stringConsumerRecord.value();
                logger.debug("Reading data " + stringConsumerRecord.topic() + " partition " + topicPartiton
                        + "  Offset " + offset + "Message " + message);
                exec.submit(new MessageProcessor(stringConsumerRecord));
            }
        }
        consumer.commitSync();
    }
}
