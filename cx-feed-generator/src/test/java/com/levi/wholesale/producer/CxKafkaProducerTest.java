package com.levi.wholesale.producer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.common.model.ErrorEvent;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static com.levi.wholesale.util.TestUtility.getListAppenderForClass;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CxKafkaProducerTest {

    @InjectMocks
    private CxKafkaProducer cxKafkaProducer;

    @Mock
    private KafkaProducer<String, String> kafkaProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, String>> recordArgumentCaptor;

    @Captor
    private ArgumentCaptor<Callback> callbackArgumentCaptor;

    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(CxKafkaProducer.class);
    }

    @Test
    void testPublishOrderDetailsToKafka_ShouldSendMessageToTopic() throws IOException {
        cxKafkaProducer.publishOrderDetailsToKafka("{}", "test_sdn", "TestCxTopic");
        Mockito.verify(kafkaProducer).send(recordArgumentCaptor.capture(), callbackArgumentCaptor.capture());
        ProducerRecord<String, String> record = recordArgumentCaptor.getValue();
        Assertions.assertEquals("test_sdn", record.key());
        Assertions.assertEquals("TestCxTopic", record.topic());
        Assertions.assertEquals("{}", record.value());

        Callback callback = callbackArgumentCaptor.getValue();
        TopicPartition topicPartition = new TopicPartition("TestTopic", 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 100, 1, 12121212L, 32, 20);
        callback.onCompletion(recordMetadata, null);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("Message Published to kafka Topic"));
    }

    @Test
    void testPublishOrderDetailsToKafka_ShouldSendMessageToErrTopic() throws IOException {
        when(kafkaProducer.send(any(ProducerRecord.class), any(Callback.class)))
                .thenThrow(RuntimeException.class)
                .thenReturn(null);
        when(objectMapper.writeValueAsString(any(ErrorEvent.class))).thenReturn("{}");
        ReflectionTestUtils.setField(cxKafkaProducer, "errorTopic", "TestErrTopic");

        cxKafkaProducer.publishOrderDetailsToKafka("{}", "test_sdn", "TestCxTopic");

        Mockito.verify(kafkaProducer, Mockito.times(2)).send(recordArgumentCaptor.capture(), callbackArgumentCaptor.capture());
        ProducerRecord<String, String> record = recordArgumentCaptor.getValue();
        Assertions.assertNotNull(record.key());
        Assertions.assertEquals("TestErrTopic", record.topic());
        Assertions.assertEquals("{}", record.value());

        Callback callback = callbackArgumentCaptor.getValue();
        TopicPartition topicPartition = new TopicPartition("TestTopic", 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 100, 1, 12121212L, 32, 20);
        callback.onCompletion(recordMetadata, null);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("Message Published to Error kafka Topic TestTopic with offset"));
    }

    @Test
    void testPublishOrderDetailsToKafka_ShouldNotSendMessageToErrTopic() throws IOException {
        when(kafkaProducer.send(any(ProducerRecord.class), any(Callback.class)))
                .thenThrow(RuntimeException.class)
                .thenReturn(null);
        when(objectMapper.writeValueAsString(any(ErrorEvent.class))).thenReturn("{}");
        ReflectionTestUtils.setField(cxKafkaProducer, "errorTopic", "TestErrTopic");

        cxKafkaProducer.publishOrderDetailsToKafka("{}", "test_sdn", "TestCxTopic");

        Mockito.verify(kafkaProducer, Mockito.times(2)).send(recordArgumentCaptor.capture(), callbackArgumentCaptor.capture());
        ProducerRecord<String, String> record = recordArgumentCaptor.getValue();
        Assertions.assertNotNull(record.key());
        Assertions.assertEquals("TestErrTopic", record.topic());
        Assertions.assertEquals("{}", record.value());

        Callback callback = callbackArgumentCaptor.getValue();
        TopicPartition topicPartition = new TopicPartition("TestTopic", 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 100, 1, 12121212L, 32, 20);

        try {
            callback.onCompletion(recordMetadata, new Exception());
        } catch (KafkaException ex) {
            assertEquals("Failed to publish Message", ex.getMessage());
        }

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("Failed to publish Message"));
    }
}