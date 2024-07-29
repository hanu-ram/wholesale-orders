package com.levi.wholesale.service;

import com.levi.wholesale.config.KafkaConfig;
import com.levi.wholesale.lambda.common.config.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class ErrorServiceTest {

    @Test
    void testSendToErrorTopic() {
        ErrorService errorService = new ErrorService();

        KafkaProducer kafkaProducer = mock(KafkaProducer.class);

        MockedStatic<KafkaConfig> kafkaConfigMockedStatic = mockStatic(KafkaConfig.class);
        kafkaConfigMockedStatic.when(KafkaConfig::getProducer).thenReturn(kafkaProducer);

        MockedStatic<Configuration> configurationMockedStatic = mockStatic(Configuration.class);
        configurationMockedStatic.when(Configuration::getKafkaErrorTopic).thenReturn("test-error-topic");

        errorService.sendToErrorTopic("{}");

        verify(kafkaProducer).send(any(ProducerRecord.class));

        kafkaConfigMockedStatic.close();
        configurationMockedStatic.close();
    }

}