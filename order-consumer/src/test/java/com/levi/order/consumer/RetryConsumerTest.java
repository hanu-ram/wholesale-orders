package com.levi.order.consumer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.levi.config.OrderLoaderConfig;
import com.levi.order.service.OrderProcessingService;
import com.levi.order.service.RetryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.levi.common.constant.Constants.OC_PROCESS_FAILURE_ALERT;
import static com.levi.order.test.util.TestUtility.getListAppenderForClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryConsumerTest {
    @Mock
    private OrderProcessingService orderProcessingService;
    @Mock
    private OrderLoaderConfig orderLoaderConfig;
    @Mock
    private KafkaConsumer<String, String> kafkaConsumer;
    @Mock
    private RetryService retryService;
    @InjectMocks
    private RetryConsumer retryConsumer;

    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(RetryConsumer.class);
    }

    @Test
    void testRetryConsumer() throws JsonProcessingException {
        testMockSetup(false);
        ReflectionTestUtils.setField(retryConsumer, "timeOut", 100L);
        ReflectionTestUtils.setField(retryConsumer, "retryTime", 1000L);
        ReflectionTestUtils.setField(retryConsumer, "retryTopicName", "TestTopic");
        when(orderProcessingService.validateAndPopulateOrderList(anyString(), any(List.class))).thenReturn(true);
        when(orderLoaderConfig.getRetryKafkaConsumer()).thenReturn(kafkaConsumer);
        doNothing().when(kafkaConsumer).subscribe(Collections.singleton("TestTopic"));

        retryConsumer.retryConsumer();

        verify(kafkaConsumer, atLeastOnce()).commitAsync();
        verify(orderProcessingService, atLeastOnce()).saveOrderDataList(any(List.class));
    }

    @Test
    void retryConsumer_ShouldNotSaveOrderData() throws JsonProcessingException {
        testMockSetup(true);
        ReflectionTestUtils.setField(retryConsumer, "timeOut", 100L);
        ReflectionTestUtils.setField(retryConsumer, "retryTime", 1000L);
        ReflectionTestUtils.setField(retryConsumer, "retryTopicName", "TestTopic");
        when(orderLoaderConfig.getRetryKafkaConsumer()).thenReturn(kafkaConsumer);
        doNothing().when(kafkaConsumer).subscribe(Collections.singleton("TestTopic"));

        retryConsumer.retryConsumer();

        verify(orderProcessingService, never()).validateAndPopulateOrderList(anyString(), any(List.class));
        verify(orderProcessingService, never()).saveOrderDataList(any(List.class));
    }

    @Test
    void testRetryConsumer_sendDataToRetryTopic_onException() throws JsonProcessingException {
        testMockSetup(false);

        ReflectionTestUtils.setField(retryConsumer, "timeOut", 100L);
        ReflectionTestUtils.setField(retryConsumer, "retryTime", 1000L);
        ReflectionTestUtils.setField(retryConsumer, "retryTopicName", "TestTopic");

        when(orderProcessingService.validateAndPopulateOrderList(anyString(), any(List.class))).thenReturn(false);
        when(orderLoaderConfig.getRetryKafkaConsumer()).thenReturn(kafkaConsumer);
        doNothing().when(kafkaConsumer).subscribe(Collections.singleton("TestTopic"));

        retryConsumer.retryConsumer();

        verify(retryService, atLeastOnce()).prepareRetryListAndSendToRetryTopic(any(ConsumerRecords.class));
    }

    @Test
    void testRetryConsumer_catchException() {
        testMockSetup(false);

        ReflectionTestUtils.setField(retryConsumer, "timeOut", 100L);
        ReflectionTestUtils.setField(retryConsumer, "retryTime", 100L);
        ReflectionTestUtils.setField(retryConsumer, "retryTopicName", "TestTopic");

        when(orderProcessingService.validateAndPopulateOrderList(anyString(), any(List.class))).thenThrow(RuntimeException.class);
        when(orderLoaderConfig.getRetryKafkaConsumer()).thenReturn(kafkaConsumer);
        doNothing().when(kafkaConsumer).subscribe(Collections.singleton("TestTopic"));

        retryConsumer.retryConsumer();

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(a -> a.startsWith(OC_PROCESS_FAILURE_ALERT + " : Exception when consuming data from retry topic. "));
    }


    private void testMockSetup(boolean setupEmptyRecord) {
        Map<TopicPartition, List<ConsumerRecord<String, String>>> consumerRecordMap = new HashMap<>();
        TopicPartition partition = new TopicPartition("TestTopic", 1);
        ConsumerRecord<String, String> consumerRecord1 = new ConsumerRecord<>("TestTopic", 1, 100L, "testKey", "{}");

        consumerRecordMap.put(partition, List.of(consumerRecord1));
        ConsumerRecords<String, String> records;

        if (setupEmptyRecord) {
            records = new ConsumerRecords<>(new HashMap<>());
        } else {
            records = new ConsumerRecords<>(consumerRecordMap);
        }

        when(kafkaConsumer.poll(100L)).thenReturn(records);
    }
}