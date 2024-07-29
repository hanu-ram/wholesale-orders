package com.levi.kafka.app.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfigProperties {

    @Value("${kafka.msg.consumer.bootstrapHostAndPort}")
    private String bootstrapHostAndPort;

    @Value("${kafka.msg.consumer.autoOffsetReset}")
    private String autoOffsetReset;

    @Value("${kafka.msg.consumer.enableAutoCommit}")
    private String enableAutoCommit;

    @Value("${kafka.msg.consumer.group.id}")
    private String groupId;

    @Value("${kafka.auto.commit.interval.ms}")
    private String commitInterval;

    public String getBootstrapHostAndPort() {
        return bootstrapHostAndPort;
    }

    public void setBootstrapHostAndPort(String bootstrapHostAndPort) {
        this.bootstrapHostAndPort = bootstrapHostAndPort;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public String getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(String enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCommitInterval() {
        return commitInterval;
    }

    public void setCommitInterval(String commitInterval) {
        this.commitInterval = commitInterval;
    }

    public void setNumberThreadsConsumer(String numberThreadsConsumer) {
        this.numberThreadsConsumer = numberThreadsConsumer;
    }

    @Value("{kafka.consumer.number}")
    private String numberThreadsConsumer;

    @Bean
    @Qualifier("ConsumerProperties")
    Properties getKafkaSubProps() {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapHostAndPort);
        props.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, commitInterval);
        props.setProperty(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }

    public String getNumberThreadsConsumer() {
        return numberThreadsConsumer;
    }

}

