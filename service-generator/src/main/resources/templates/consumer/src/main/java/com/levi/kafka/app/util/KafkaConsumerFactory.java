package com.levi.kafka.app.util;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public final class KafkaConsumerFactory {
    public KafkaConsumer<String, String> createConsumer(Properties properties) {
        return new KafkaConsumer<String, String>(properties);
    }
}
