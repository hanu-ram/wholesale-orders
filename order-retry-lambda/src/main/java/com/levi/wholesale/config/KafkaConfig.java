package com.levi.wholesale.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Properties;

import static com.levi.wholesale.lambda.common.config.Configuration.getApiKey;
import static com.levi.wholesale.lambda.common.config.Configuration.getBootStrapHost;
import static com.levi.wholesale.lambda.common.config.Configuration.getGroupId;
import static com.levi.wholesale.lambda.common.config.Configuration.getKeyPassword;
import static com.levi.wholesale.lambda.common.config.Configuration.getKeyStoreLocation;
import static com.levi.wholesale.lambda.common.config.Configuration.getKeyStorePassword;
import static com.levi.wholesale.lambda.common.config.Configuration.getProducerAck;
import static com.levi.wholesale.lambda.common.config.Configuration.getProducerRetries;
import static com.levi.wholesale.lambda.common.config.Configuration.getSecretKey;
import static com.levi.wholesale.lambda.common.config.Configuration.shouldUseSASL;
import static com.levi.wholesale.lambda.common.config.Configuration.shouldUseSSL;

public final class KafkaConfig {

    private KafkaConfig() {
    }

    private static Properties getKafkaRetryProducerProps() {
        Properties props = new Properties();

        String username = "username='" + getApiKey() + "'";
        String password = "password='" + getSecretKey() + "'";

        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootStrapHost());

        if (Boolean.parseBoolean(shouldUseSSL())) {
            String resourceDir = new File(KafkaConfig.class.getClassLoader().getResource("application.properties").getPath()).getParent();
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceDir
                    + FileSystems.getDefault().getSeparator() + getKeyStoreLocation());
            props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getKeyStorePassword());
            props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, getKeyPassword());
        }
        props.setProperty(ProducerConfig.ACKS_CONFIG, getProducerAck());
        props.setProperty(ProducerConfig.RETRIES_CONFIG, getProducerRetries());
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        if (Boolean.parseBoolean(shouldUseSASL())) {
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " + username + password + ";");
            props.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return props;
    }

    private static Properties getKafkaConsumerProps() {
        Properties props = new Properties();

        String username = "username='" + getApiKey() + "'";
        String password = "password='" + getSecretKey() + "'";

        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootStrapHost());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());

        if (Boolean.parseBoolean(shouldUseSSL())) {
            String resourceDir = new File(KafkaConfig.class.getClassLoader().getResource("application.properties").getPath()).getParent();
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceDir
                    + FileSystems.getDefault().getSeparator() + getKeyStoreLocation());
            props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getKeyStorePassword());
            props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, getKeyPassword());
        }
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        if (Boolean.parseBoolean(shouldUseSASL())) {
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " + username + password + ";");
            props.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return props;
    }

    public static KafkaProducer<String, String> getProducer() {
        return new KafkaProducer<>(getKafkaRetryProducerProps());
    }

    public static KafkaConsumer<String, String> getKafkaConsumer() {
        return new KafkaConsumer<>(getKafkaConsumerProps());
    }
}
