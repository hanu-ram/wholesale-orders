
package com.levi.order.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.config.OrderLoaderConfig;
import com.levi.util.OrderLoaderUtility;
import com.levi.wholesale.common.dto.OrderData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.levi.order.test.util.TestUtility.getListAppenderForClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryServiceTest {

    @InjectMocks
    private RetryService retryService;

    @Mock
    private OrderLoaderConfig orderLoaderConfig;

    @Mock
    private OrderLoaderUtility orderLoaderUtility;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private KafkaProducer<String, String> kafkaProducer;
    @Captor
    private ArgumentCaptor<Callback> captor;
    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(RetryService.class);
    }

    @Test
    void testSendToRetryTopic_returnsSuccess() {

        ReflectionTestUtils.setField(retryService, "retryTopic", "TestTopic");

        retryService.sendToRetryTopic("{}", "{}");

        verify(kafkaProducer).send(any(ProducerRecord.class), captor.capture());
        TopicPartition topicPartition = new TopicPartition("TestTopic", 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 100, 1, 12121212L, 32, 20);
        captor.getValue().onCompletion(recordMetadata, null);

        Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("Message Published to kafka Topic"));
    }

    @Test
    void testSendToRetryTopic_returnsError() {
        final ListAppender<ILoggingEvent> listAppenderForClass = getListAppenderForClass(RetryService.class);

        ReflectionTestUtils.setField(retryService, "retryTopic", "TestTopic");
        retryService.sendToRetryTopic("{}", "{}");
        verify(kafkaProducer).send(any(ProducerRecord.class), captor.capture());

        TopicPartition topicPartition = new TopicPartition("TestTopic", 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 100, 1, 12121212L, 32, 20);
        captor.getValue().onCompletion(recordMetadata, new Exception());

        Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("Failed to publish payload:"));
    }

    @Test
    void testSendToErrorTopic() {
        ReflectionTestUtils.setField(retryService, "errorTopic", "TestTopic");

        retryService.sendToErrorTopic("", "{}");

        verify(kafkaProducer).send(any(ProducerRecord.class), any(Callback.class));
    }

    @Test
    void testPrepareRetryListAndSendToRetryTopic_sendDataToErrorTopic() throws JsonProcessingException {
        OrderData orderData = getOrderData(4);
        String jsonToSend = "{\"module\":\"order-loader\"}";

        ReflectionTestUtils.setField(retryService, "maxRetry", 3);
        ReflectionTestUtils.setField(retryService, "errorTopic", "TestTopic");
        when(objectMapper.writeValueAsString(orderData)).thenReturn(jsonToSend);
        ConsumerRecords<String, String> records = getConsumerRecords(jsonToSend);
        when(orderLoaderUtility.getOrderDataFromJsonString(jsonToSend)).thenReturn(orderData);
        when(objectMapper.writeValueAsString(orderData)).thenReturn(jsonToSend);

        retryService.prepareRetryListAndSendToRetryTopic(records);

        verify(kafkaProducer).send(any(ProducerRecord.class), any(Callback.class));
    }

    @Test
    void testPrepareRetryListAndSendToRetryTopic_sendDataToRetryTopic() throws JsonProcessingException {
        OrderData orderData = getOrderData(1);
        String jsonToSend = "{\"module\":\"order-loader\"}";

        ReflectionTestUtils.setField(retryService, "maxRetry", 3);
        ReflectionTestUtils.setField(retryService, "retryTopic", "TestTopic");
        when(objectMapper.writeValueAsString(orderData)).thenReturn(jsonToSend);
        ConsumerRecords<String, String> records = getConsumerRecords(jsonToSend);
        when(orderLoaderUtility.getOrderDataFromJsonString(jsonToSend)).thenReturn(orderData);
        when(objectMapper.writeValueAsString(orderData)).thenReturn(jsonToSend);

        retryService.prepareRetryListAndSendToRetryTopic(records);

        verify(kafkaProducer).send(any(ProducerRecord.class), any(Callback.class));
    }

    private static OrderData getOrderData(int retryCount) {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData orderData = getValidOrderData();
        orderData.setRetryCount(retryCount);
        orderDataList.add(orderData);
        return orderData;
    }

    private static OrderData getValidOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSoldTo("TestSoldTo");
        orderData.setPurchaseOrderNumber("TestPurchaseOrderNumber");
        orderData.setSalesDocumentNumber("TestSalesDocumentNumber");
        orderData.setCurrency("USD");
        orderData.setRegion("TestRegion");
        orderData.setCountryCode("TestCountryCode");
        orderData.setMaterialCode("TestMaterialCode");
        orderData.setLineItem("TestLineItem");
        orderData.setSchLineItem("TestSchLineItem");
        orderData.setMaterialSize("TestMaterialSize");
        orderData.setSalesDocumentDate(Date.valueOf("2023-1-31"));
        return orderData;
    }

    private static ConsumerRecords<String, String> getConsumerRecords(String jsonToSend) {
        TopicPartition partition = new TopicPartition("TestTopic", 0);

        Map<TopicPartition, List<ConsumerRecord<String, String>>> map = new HashMap<>();
        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("TestTopic", 0, 100, "testKey", jsonToSend);
        map.put(partition, List.of(consumerRecord));
        return new ConsumerRecords<>(map);
    }
}
