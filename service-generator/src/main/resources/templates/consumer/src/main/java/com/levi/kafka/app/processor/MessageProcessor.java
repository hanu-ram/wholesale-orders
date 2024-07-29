package com.levi.kafka.app.processor;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
    private String messages;

    public MessageProcessor(ConsumerRecord stringConsumerRecord) {
        this.stringConsumerRecord = stringConsumerRecord;
    }

    public ConsumerRecord getStringConsumerRecord() {
        return stringConsumerRecord;
    }

    public void setStringConsumerRecord(ConsumerRecord stringConsumerRecord) {
        this.stringConsumerRecord = stringConsumerRecord;
    }

    private ConsumerRecord stringConsumerRecord;

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    @Override
    public void run() {
        // Create an instance of StopWatch.
        StopWatch stopWatch = new StopWatch();
        // Start the watch, do some task and stop the watch.
        stopWatch.start();
        // This method will have DB persistence logic
        processMessage();
        stopWatch.stop();
        logger.info("Time taken to process message : " + stopWatch.getTime());
    }

    public void processMessage() {
        logger.info("Messages Processed" + stringConsumerRecord.value());
        logger.info("Offset is :" + stringConsumerRecord.offset());
        logger.info("Topic is :" + stringConsumerRecord.topic());
        logger.info("Partition is :" + stringConsumerRecord.partition());
        logger.info("Thread Name = " + Thread.currentThread().getName());
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            logger.error("Error while processing the message", e);
        }
    }
}
