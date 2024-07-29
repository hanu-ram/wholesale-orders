package com.levi.wholesale.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.levi.common.constant.Constants;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class CxFeedConfig {

    @Bean("jpaTransactionManager")
    @Primary
    public JpaTransactionManager jpaTransactionManager(DataSource dataSource) {
        final JpaTransactionManager tm = new JpaTransactionManager();
        tm.setDataSource(dataSource);
        return tm;
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        mapper.setDateFormat(dateFormat);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Value("${kafka.msg.producer.bootstrapHostAndPort}")
    private String bootstrapHostAndPort;

    @Value("${kafka.msg.producer.group.id}")
    private String producerGroupId;

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

    @Value("${kafka.config.api.secrete}")
    private String secrete;

    @Bean("kafkaRetryProducerProps")
    public Properties getKafkaRetryProducerProps() {
        Properties props = new Properties();

        String username = "username='" + apiKey + "'";
        String password = "password='" + secrete + "'";

        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHostAndPort);
        props.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, producerGroupId);

        if (useSsl) {
            String resourceDir = new File(CxFeedConfig.class.getClassLoader().getResource("application.properties").getPath()).getParent();
            props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceDir
                    + FileSystems.getDefault().getSeparator() + System.getenv("KEY_STORE_LOCATION"));
            props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, System.getenv("KEY_STORE_PASSWORD"));
            props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, System.getenv("KEY_STORE_PASSWORD"));
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

    @Bean("kafkaProducer")
    public KafkaProducer<String, String> getProducer() {
        return new KafkaProducer<>(getKafkaRetryProducerProps());
    }
}
