package com.levi.order.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(MockitoExtension.class)
class InitOrderConsumerTest {

    @InjectMocks
    private InitOrderConsumer initOrderConsumer;

    @Mock
    private OrderConsumer orderConsumer;

    @Mock
    ExecutorService executorService;

    @Test
    void testInitConsumer() {

        MockedStatic<Executors> executorsMockedStatic = Mockito.mockStatic(Executors.class);

        ReflectionTestUtils.setField(initOrderConsumer, "numberOfConsumers", 3);
        executorsMockedStatic.when(() -> Executors.newFixedThreadPool(3)).thenReturn(executorService);
        Mockito.when(orderConsumer.createKafkaConsumer()).thenReturn(() -> {
        });

        initOrderConsumer.initConsumer();
        Mockito.verify(executorService, Mockito.times(3)).submit(Mockito.any(Runnable.class));

    }
}