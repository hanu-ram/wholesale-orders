package com.levi.wholesale.service;

import com.levi.wholesale.config.KafkaConfig;
import com.levi.wholesale.lambda.common.config.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.UUID;

public class ErrorService {

    public void sendToErrorTopic(String message) {
        KafkaProducer<String, String> producer = KafkaConfig.getProducer();
        String key = UUID.randomUUID().toString();
        producer.send(new ProducerRecord<>(Configuration.getKafkaErrorTopic(), key, message));
    }
}
