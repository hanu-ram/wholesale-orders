package com.levi.kafka.app.config;


import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfigProperties {

    @Value("${kafka.msg.producer.bootstrapHostAndPort}")
    private String bootstrapHostAndPort;

    @Value("${kafka.msg.producer.group.id}")
    private String groupId;

    @Value("${kafka.msg.producer.acks}")
    private String acks;

    @Value("${kafka.msg.producer.retries}")
    private String retries;

    @Value("${kafka.producer.batch.size}")
    private String batchSize;

    @Value("${kafka.producer.max.block.ms}")
    private String maxBlock;

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getMaxBlock() {
        return maxBlock;
    }

    public void setMaxBlock(String maxBlock) {
        this.maxBlock = maxBlock;
    }

    public String getBootstrapHostAndPort() {
        return bootstrapHostAndPort;
    }

    public void setBootstrapHostAndPort(String bootstrapHostAndPort) {
        this.bootstrapHostAndPort = bootstrapHostAndPort;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    @Bean
    @Qualifier("ProducerProperties")
    Properties getKafkaProducerProps() {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapHostAndPort);
        props.setProperty(ProducerConfig.ACKS_CONFIG, acks);
        props.setProperty(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlock);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }

}
