package com.levi.kafka.app.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Properties;
import java.util.UUID;

@Service
public class KafkaPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisher.class);
    @Autowired
    @Qualifier("ProducerProperties")
    private Properties props;

    @Value("${kafka.msg.producer.retries}")
    private int retries;

    @Value("${kafka.producer.retry.backoff.ms}")
    private int retryBackOffMs;

    @Value("${kafka.producer.retry.backoff.max.ms}")
    private int retryBackOffMaxMs;

    @Value("${kafka.producer.exponential.backoff}")
    private boolean exponentialBackOff;

    private Producer<String, Object> initProducer() {
        return new KafkaProducer<>(props);
    }

    public boolean publish(String topic, String message) throws InterruptedException {
        Producer<String, Object> producer = null;
        try {
            producer = initProducer();
            LOGGER.info("Publishing message to kafka topic " + topic + " Message Body End " + message);
            return publishToKafka(producer, topic, message, retries, retryBackOffMs);
        } finally {
            if (producer != null) {
                producer.close();
            }
        }
    }

    boolean publishToKafka(Producer<String, Object> producer, String topic, String message, int retries,
                           int retryBackOffMs) throws InterruptedException {
        String key = UUID.randomUUID().toString();
        boolean published = false;
        try {
            producer.send(new ProducerRecord<>(topic, key, message)).get();
            LOGGER.info("Message published successfully");
            published = true;
        } catch (Exception e) {
            LOGGER.error("Error occured while publishing message" + e);
            if (e.getCause() instanceof RetriableException) {
                published = retryPublish(producer, topic, message, retries, retryBackOffMs);
            }
        }
        return published;
    }

    private boolean retryPublish(Producer<String, Object> producer, String topic, String message, int retries,
                                 int retryBackOffMs) throws InterruptedException {
        boolean published = false;
        while (retries > 0) {
            retries--;
            if (retryBackOffMs > 0) {
                if (retryBackOffMaxMs <= 0 || (retryBackOffMs <= retryBackOffMaxMs)) {
                    LOGGER.info("Will retry after milliseconds..." + retryBackOffMs);
                    Thread.sleep(retryBackOffMs);
                } else {
                    LOGGER.info("Will retry after milliseconds..." + retryBackOffMaxMs);
                    Thread.sleep(retryBackOffMaxMs);
                }
                if (exponentialBackOff) {
                    retryBackOffMs = retryBackOffMs * retries;
                }
                LOGGER.info("Restarted publish attempt...");
                published = publishToKafka(producer, topic, message, retries, retryBackOffMs);
                if (published)
                    break;
            }
        }
        return published;
    }
}
