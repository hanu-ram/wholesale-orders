package com.levi.wholesale.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.wholesale.config.KafkaConfig;
import com.levi.wholesale.config.ObjectMapperConfig;
import com.levi.wholesale.domain.OrderDetails;
import com.levi.wholesale.producer.OrderDetailsProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.List;
import java.util.concurrent.Future;

@Slf4j
public class PersistenceManager {

    private OrderDetailsProducer orderDetailsProducer;

    public PersistenceManager(OrderDetailsProducer orderDetailsProducer) {
        this.orderDetailsProducer = orderDetailsProducer;
    }

    public void addOrderDetailsToKafka(OrderDetails orderDetailsFeed,
                                       String feedFile, List<Future<RecordMetadata>> futuresList) {
        KafkaProducer<String, String> producer = KafkaConfig.getKafkaProducer();
        try {
            log.info("Publishing the order detail SD {} received in feed file {} ",
                    orderDetailsFeed.getSalesDocumentNumber(), feedFile);
            ObjectMapper mapper = ObjectMapperConfig.getObjectMapper();
            String orderDetailsJson = mapper.writeValueAsString(orderDetailsFeed);
            orderDetailsProducer.publishOrderDetailsToKafka(producer, orderDetailsJson,
                    orderDetailsFeed.getSalesDocumentNumber(), futuresList);
        } catch (Exception ex) {
            log.error("Exception while publishing :- ", ex);
        }
    }
}
