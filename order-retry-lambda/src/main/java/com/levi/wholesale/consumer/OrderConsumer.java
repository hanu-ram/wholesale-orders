package com.levi.wholesale.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.wholesale.config.KafkaConfig;
import com.levi.wholesale.lambda.common.config.Configuration;
import com.levi.wholesale.lambda.common.domain.dao.ErrorDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.domain.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.ScheduleLineEntryDao;
import com.levi.wholesale.service.ErrorService;
import com.levi.wholesale.service.OrderDetailsRetryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.Connection;
import java.util.Collections;

public class OrderConsumer {
    public void consumeOrder(Connection connection)  {
        KafkaConsumer<String, String> kafkaConsumer = KafkaConfig.getKafkaConsumer();
        kafkaConsumer.subscribe(Collections.singleton(Configuration.getRetryTopic()));

        do {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.parseLong(Configuration.getPollTimeOut()));
            for (ConsumerRecord<String, String> rec : records) {
                getOrderDetailsService().saveOrderDetails(rec.value(), connection, 0);
            }
            kafkaConsumer.commitSync();
        } while (true);
    }

    private OrderDetailsRetryService getOrderDetailsService() {
        return new OrderDetailsRetryService(new OrderDetailsDao(), new LineEntryDao(),
                new ScheduleLineEntryDao(), new ErrorDetailsDao(), new ObjectMapper(), new ErrorService());
    }
}
