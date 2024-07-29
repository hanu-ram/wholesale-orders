package com.levi.order.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class InitOrderConsumer {

    @Value("${kafka.order.consumer.thread}")
    private int numberOfConsumers;

    private final OrderConsumer orderConsumer;

    public InitOrderConsumer(OrderConsumer orderConsumer) {
        this.orderConsumer = orderConsumer;
    }

    public void initConsumer() {
        ExecutorService exec = Executors.newFixedThreadPool(numberOfConsumers);
        for (int i = 0; i < numberOfConsumers; i++) {
            log.info("Starting consumer " + i);
            exec.submit(orderConsumer.createKafkaConsumer());
        }
    }
}
