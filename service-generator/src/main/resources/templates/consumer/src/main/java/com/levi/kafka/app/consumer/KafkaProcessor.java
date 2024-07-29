package com.levi.kafka.app.consumer;

import com.levi.kafka.app.util.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class KafkaProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProcessor.class);

    private AtomicBoolean stopExecution = new AtomicBoolean(false);
    private final KafkaConsumerFactory kafkaConsumerFactory;
    private final ConsumerProcessor processor;
    private final String topicName;
    private final Properties consumerProp;
    private final int numberOfConsumerThreads;

    public KafkaProcessor(
            ConsumerProcessor processor,
            KafkaConsumerFactory kafkaConsumerFactory,
            Properties consumerProp,
            String topicName,
            int numberOfConsumerThreads) {
        this.kafkaConsumerFactory = kafkaConsumerFactory;
        this.processor = processor;
        this.topicName = topicName;
        this.consumerProp = consumerProp;
        this.numberOfConsumerThreads = numberOfConsumerThreads;
    }

    public AtomicBoolean getStopExecution() {
        return this.stopExecution;
    }

    public void setStopExecution(AtomicBoolean stopExecution) {
        this.stopExecution = stopExecution;
    }

    public KafkaConsumerFactory getKafkaConsumerFactory() {
        return kafkaConsumerFactory;
    }

    public ConsumerProcessor getProcessor() {
        return processor;
    }

    public String getTopicName() {
        return topicName;
    }

    public Properties getConsumerProp() {
        return consumerProp;
    }

    public int getNumberOfConsumerThreads() {
        return numberOfConsumerThreads;
    }

    @Override
    public void run() {
        KafkaConsumer<String, String> consumer = this.kafkaConsumerFactory.createConsumer(this.consumerProp);
        try {
            logger.info("Creating fixed thread pool executor. num-threads-in-pool={}", numberOfConsumerThreads);
            ExecutorService exec = Executors.newFixedThreadPool(numberOfConsumerThreads);
            logger.info("Subscribing consumer to topic. topic={}", this.topicName);
            consumer.subscribe(Collections.singleton(this.topicName),
                    (ConsumerRebalanceListener) (new ConsumerRebalanceListener() {
                        public void onPartitionsRevoked(Collection partitions) {
                            logger.debug("Partitions revoked. partitions={}", partitions);
                        }

                        public void onPartitionsAssigned(Collection partitions) {
                            logger.debug("Partitions assigned. partitions={}", partitions);
                        }
                    }));
            while (!this.stopExecution.get()) {
                logger.info("Listening to the Topic by " + Thread.currentThread().getId());
                processor.processInLoop(consumer, exec);
            }
            logger.info("Shutting down pools");
            exec.shutdown();
        } finally {
            consumer.close();
        }
    }
}
