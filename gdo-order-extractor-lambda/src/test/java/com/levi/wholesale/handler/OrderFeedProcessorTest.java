package com.levi.wholesale.handler;

import com.levi.wholesale.domain.OrderDetails;
import com.levi.wholesale.lambda.common.config.Configuration;
import com.levi.wholesale.persistence.PersistenceManager;
import com.levi.wholesale.producer.OrderDetailsProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class OrderFeedProcessorTest {

    private final MockProducer<String, String> mockProducer
            = new MockProducer<>(true, new StringSerializer(), new StringSerializer());

    PersistenceManager persistenceManager = mock(PersistenceManager.class);
    OrderDetailsProducer orderDetailsProducer = mock(OrderDetailsProducer.class);
    private static MockedStatic<Configuration> configurationMockedStatic;

    @BeforeAll
    static void setUp() {
        configurationMockedStatic = Mockito.mockStatic(Configuration.class);
        Mockito.when(Configuration.getKafkaTopic()).thenReturn("wholesale-orders");
        Mockito.when(Configuration.getKafkaErrorTopic()).thenReturn("error-topic");
        Mockito.when(Configuration.getBootStrapHost()).thenReturn("localhost:9092");
        Mockito.when(Configuration.getProducerAck()).thenReturn("all");
        Mockito.when(Configuration.getProducerRetries()).thenReturn("1");
        Mockito.when(Configuration.shouldUseSASL()).thenReturn("FALSE");
    }

    @AfterAll
    static void close() {
        configurationMockedStatic.close();
    }

    @Test
    void shouldFailToProcessFeedWhenNoDataInFile() throws Exception {
        OrderFeedProcessor feedProcessor = new OrderFeedProcessor(persistenceManager);
        String fileName = "salesorder_empty.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        feedProcessor.processFeed(fileName, classPathResource.getInputStream());
        verify(persistenceManager, times(0)).addOrderDetailsToKafka(any(), eq(fileName), anyList());
    }

    @Test
    void addOrderDetailsToKafkaTest() throws Exception {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setSalesDocumentNumber("1");
        orderDetails.setMaterialCode("1234");
        orderDetails.setLineItem("1");
        orderDetails.setSchLineItem("2");
        PersistenceManager persistenceManager = new PersistenceManager(orderDetailsProducer);
        persistenceManager.addOrderDetailsToKafka(orderDetails, "salesorder_new.csv", new ArrayList<>());
        verify(orderDetailsProducer, times(1))
                .publishOrderDetailsToKafka(any(), any(), any(), anyList());
    }

    @Test
    void shouldProcessFeedWhenValidFile() throws Exception {
        OrderFeedProcessor feedProcessor = new OrderFeedProcessor(persistenceManager);
        String fileName = "salesorder_new.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        feedProcessor.processFeed(fileName, classPathResource.getInputStream());
        verify(persistenceManager, times(12)).addOrderDetailsToKafka(any(), eq(fileName), anyList());
    }

    @Test
    void shouldFailToProcessFileWithInValidName() throws Exception {
        OrderFeedProcessor feedProcessor = new OrderFeedProcessor(persistenceManager);
        String fileName = "InvalidFileFormat.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        Assertions.assertEquals("failure", feedProcessor.processFeed(fileName, classPathResource.getInputStream()));
    }
}