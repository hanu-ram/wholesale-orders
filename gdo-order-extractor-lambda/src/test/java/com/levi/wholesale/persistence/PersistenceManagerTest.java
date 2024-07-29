package com.levi.wholesale.persistence;

import com.levi.wholesale.config.KafkaConfig;
import com.levi.wholesale.domain.OrderDetails;
import com.levi.wholesale.producer.OrderDetailsProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

public class PersistenceManagerTest {
    private OrderDetailsProducer orderDetailsProducer = mock(OrderDetailsProducer.class);
    private KafkaProducer<String, String> kafkaProducer = mock(KafkaProducer.class);

    @Test
    void testAddOrderDetailsToKafka() throws IOException {
        PersistenceManager persistenceManager = new PersistenceManager(orderDetailsProducer);
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setSalesDocumentNumber("testSalesDocumentNUmber");

        MockedStatic<KafkaConfig> kafkaConfigMockedStatic = mockStatic(KafkaConfig.class);
        kafkaConfigMockedStatic.when(KafkaConfig::getKafkaProducer).thenReturn(kafkaProducer);

        persistenceManager.addOrderDetailsToKafka(orderDetails, "", null);
        verify(orderDetailsProducer)
                .publishOrderDetailsToKafka(eq(kafkaProducer), anyString(), anyString(), eq(null));
        kafkaConfigMockedStatic.close();
    }

}