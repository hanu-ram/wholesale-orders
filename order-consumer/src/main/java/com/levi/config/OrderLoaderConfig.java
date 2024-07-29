package com.levi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;

@Component
public class OrderLoaderConfig {

    private static final String KEY_STORE_PASSWORD = "KEY_STORE_PASSWORD";

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    @Value("${kafka.msg.producer.bootstrapHostAndPort}")
    private String bootstrapHostAndPort;

    @Value("${kafka.msg.consumer.bootstrapHostAndPort}")
    private String bootstrapHostAndPortConsumer;

    @Value("${kafka.msg.producer.group.id}")
    private String producerGroupId;

    @Value("${kafka.msg.consumer.group.id}")
    private String consumerGroupId;

    @Value("${kafka.msg.retry.consumer.group.id}")
    private String retryConsumerGroupId;

    @Value("${kafka.msg.consumer.autoOffsetReset}")
    private String autoOffsetReset;

    @Value("${kafka.msg.consumer.enableAutoCommit}")
    private String enableAutoCommit;

    @Value("${kafka.auto.commit.interval.ms}")
    private String autoCommitIntervalMs;

    @Value("${kafka.msg.producer.acks}")
    private String acks;

    @Value("${kafka.msg.producer.retries}")
    private String retries;

    @Value("${kafka.producer.batch.size}")
    private String batchSize;

    @Value("${kafka.producer.max.block.ms}")
    private String maxBlock;

    @Value("${kafka.producer.use.sasl}")
    private boolean useSasl;

    @Value("${kafka.producer.use.ssl}")
    private boolean useSsl;

    @Value("${kafka.config.api.key}")
    private String apiKey;

    @Value("${kafka.consumer.max.poll.records}")
    private String consumerMaxPollRecords;

    @Value("${kafka.config.api.secrete}")
    private String secrete;

    @Bean("kafkaRetryProducerProps")
    public Properties getKafkaRetryProducerProps() {
        Properties props = new Properties();

        String username = "username='" + apiKey + "'";
        String password = "password='" + secrete + "'";

        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHostAndPort);
        props.setProperty(GROUP_ID_CONFIG, producerGroupId);

        if (useSsl) {
            String resourceDir = new File(OrderLoaderConfig.class.getClassLoader().getResource("application.properties").getPath()).getParent();
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceDir
                    + FileSystems.getDefault().getSeparator() + System.getenv("KEY_STORE_LOCATION"));
            props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv(KEY_STORE_PASSWORD));
            props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, System.getenv(KEY_STORE_PASSWORD));
        }
        props.setProperty(ProducerConfig.ACKS_CONFIG, acks);
        props.setProperty(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlock);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        if (useSasl) {
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " + username + password + ";");
            props.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return props;
    }

    @Bean("kafkaConsumerProps")
    public Properties getKafkaConsumerProps() {
        Properties props = new Properties();

        String username = "username='" + apiKey + "'";
        String password = "password='" + secrete + "'";

        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHostAndPortConsumer);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerMaxPollRecords);

        if (useSsl) {
            String resourceDir = new File(OrderLoaderConfig.class.getClassLoader().getResource("application.properties").getPath()).getParent();
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceDir
                    + FileSystems.getDefault().getSeparator() + System.getenv("KEY_STORE_LOCATION"));
            props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv(KEY_STORE_PASSWORD));
            props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, System.getenv(KEY_STORE_PASSWORD));
        }
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        if (useSasl) {
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " + username + password + ";");
            props.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return props;
    }

    @Bean("kafkaRetryProducer")
    public KafkaProducer<String, String> getProducer() {
        return new KafkaProducer<>(getKafkaRetryProducerProps());
    }

    @Bean("kafkaOrderConsumer")
    public KafkaConsumer<String, String> getKafkaConsumer() {
        Properties kafkaConsumerProps = getKafkaConsumerProps();
        kafkaConsumerProps.setProperty(GROUP_ID_CONFIG, consumerGroupId);
        return new KafkaConsumer<>(kafkaConsumerProps);
    }

    @Bean("kafkaRetryConsumer")
    public KafkaConsumer<String, String> getRetryKafkaConsumer() {
        Properties kafkaConsumerProps = getKafkaConsumerProps();
        kafkaConsumerProps.setProperty(GROUP_ID_CONFIG, retryConsumerGroupId);
        return new KafkaConsumer<>(kafkaConsumerProps);
    }
}
