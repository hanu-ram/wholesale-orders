package com.levi.wholesale.config;

import com.levi.wholesale.lambda.common.config.Configuration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Properties;

public final class KafkaConfig {
    private KafkaConfig() {
    }

    private static KafkaProducer<String, String> kafkaProducer;

    public static KafkaProducer<String, String> getKafkaProducer() {
        if (kafkaProducer == null) {
            kafkaProducer = new KafkaProducer<>(getKafkaProducerProps());
        }
        return kafkaProducer;
    }

    public static Properties getKafkaProducerProps() {
        Properties props = new Properties();
        String userName = "username='" + Configuration.getApiKey() + "'";
        String pwd = " password='" + Configuration.getApiSecret() + "'";
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configuration.getBootStrapHost());
        props.setProperty(ProducerConfig.ACKS_CONFIG, Configuration.getProducerAck());
        props.setProperty(ProducerConfig.RETRIES_CONFIG, Configuration.getProducerRetries());
        if (Boolean.parseBoolean(Configuration.shouldUseSASL())) {
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " + userName + pwd + ";");
            props.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        if (Boolean.parseBoolean(Configuration.shouldUseSSL())) {
            String resourceDir = new File(KafkaConfig.class.getClassLoader().getResource("application.properties").getPath()).getParent();
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            KafkaConfig.class.getClassLoader().getResource("application.properties").getPath();
            props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceDir
                    + FileSystems.getDefault().getSeparator() + Configuration.getKeyStoreLocation()
            );
            props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, Configuration.getKeyStorePassword());
            props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, Configuration.getKeyPassword());
        }

        return props;
    }

}
