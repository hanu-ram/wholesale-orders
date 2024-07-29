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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.levi.common.constant.Constants.OC_PROCESS_FAILURE_ALERT;
import static com.levi.order.test.util.TestUtility.getListAppenderForClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private OrderProcessingService orderProcessingService;

    @Mock
    private KafkaConsumer<String, String> kafkaConsumer;

    @Mock
    private RetryService retryService;

    @Mock
    private OrderLoaderConfig orderLoaderConfig;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(OrderConsumer.class);
    }

    @Test
    void consumeOrderTest() throws JsonProcessingException {
        testMockSetup(false);
        ReflectionTestUtils.setField(orderConsumer, "runForever", false);
        ReflectionTestUtils.setField(orderConsumer, "timeOut", 1000);
        when(orderProcessingService.validateAndPopulateOrderList(anyString(), any(List.class))).thenReturn(true);
        when(orderLoaderConfig.getKafkaConsumer()).thenReturn(kafkaConsumer);
        orderConsumer.consumeOrder();

        verify(orderProcessingService).saveOrderDataList(any(List.class));
    }

    @Test
    void consumeOrder_ShouldNotSaveOrderData() throws JsonProcessingException {
        testMockSetup(true);
        ReflectionTestUtils.setField(orderConsumer, "timeOut", 1000);
        ReflectionTestUtils.setField(orderConsumer, "runForever", false);
        when(orderLoaderConfig.getKafkaConsumer()).thenReturn(kafkaConsumer);

        orderConsumer.consumeOrder();

        verify(orderProcessingService, never()).validateAndPopulateOrderList(anyString(), any(List.class));
        verify(orderProcessingService, never()).saveOrderDataList(any(List.class));
    }

    @Test
    void testCreateKafkaConsumer() {
        Assertions.assertTrue(orderConsumer.createKafkaConsumer() instanceof Runnable);
    }

    @Test
    void testConsumeOrder_sendDataToRetryTopic_onException() throws JsonProcessingException {
        testMockSetup(false);

        ReflectionTestUtils.setField(orderConsumer, "timeOut", 1000);
        ReflectionTestUtils.setField(orderConsumer, "runForever", false);
        when(orderProcessingService.validateAndPopulateOrderList(anyString(), any(List.class))).thenReturn(false);
        when(orderLoaderConfig.getKafkaConsumer()).thenReturn(kafkaConsumer);

        orderConsumer.consumeOrder();

        verify(retryService).prepareRetryListAndSendToRetryTopic(any(ConsumerRecords.class));
    }

    @Test
    void testConsumeOrder_catchException() {
        testMockSetup(false);

        ReflectionTestUtils.setField(orderConsumer, "timeOut", 1000);
        ReflectionTestUtils.setField(orderConsumer, "runForever", false);
        when(orderProcessingService.validateAndPopulateOrderList(anyString(), any(List.class))).thenThrow(RuntimeException.class);
        when(orderLoaderConfig.getKafkaConsumer()).thenReturn(kafkaConsumer);

        orderConsumer.consumeOrder();

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(a -> a.startsWith(OC_PROCESS_FAILURE_ALERT + " : Exception when consuming data"));
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

        when(kafkaConsumer.poll(1000L)).thenReturn(records);
    }
}