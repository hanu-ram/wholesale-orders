package com.levi.kafka.app.consumer;

import com.levi.kafka.app.util.KafkaConsumerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
public class ConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);
    private UUID processorId;
    private ArrayList<KafkaProcessor> consumers = new ArrayList<>();
    private ExecutorService exec;
    private final ConsumerProcessor consumerProcessor;
    private final KafkaConsumerFactory kafkaConsumerFactory;
    private final Properties consumerProp;
    @Value("${kafka.consumer.topic.name}")
    private String topicName;
    @Value("${kafka.consumer.number}")
    private int numberConsumers;
    @Value("${kafka.consumer.thread}")
    private int numberOfConsumerThreads;
    @Value("${kafka.consumer.await.termination.timeout}")
    private String awaitTerminationTimeout;

    public void setProcessorId(UUID processorId) {
        this.processorId = processorId;
    }

    public ExecutorService getExec() {
        return exec;
    }

    public void setExec(ExecutorService exec) {
        this.exec = exec;
    }

    public int getNumberConsumers() {
        return numberConsumers;
    }

    public ConsumerProcessor getConsumerProcessor() {
        return consumerProcessor;
    }

    public KafkaConsumerFactory getKafkaConsumerFactory() {
        return kafkaConsumerFactory;
    }

    public Properties getConsumerProp() {
        return consumerProp;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getNumberOfConsumerThreads() {
        return numberOfConsumerThreads;
    }

    @Autowired
    public ConsumerController(ConsumerProcessor consumerProcessor,
                              KafkaConsumerFactory kafkaConsumerFactory,
                              @Qualifier("ConsumerProperties") Properties consumerProp) {
        this.consumerProcessor = consumerProcessor;
        this.kafkaConsumerFactory = kafkaConsumerFactory;
        this.consumerProp = consumerProp;
    }

    public final UUID getProcessorId() {
        return this.processorId;
    }

    public void setProcessorId() {
        this.processorId = UUID.randomUUID();
    }

    public ArrayList<KafkaProcessor> getConsumers() {
        return this.consumers;
    }

    public void setConsumers(ArrayList<KafkaProcessor> consumers) {
        this.consumers = consumers;
    }

    @RequestMapping(path = {"/start"}, method = {RequestMethod.POST})
    public ResponseEntity<Void> start() {
        exec =  Executors.newFixedThreadPool(numberConsumers);
        if (!this.getConsumers().isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            int count = 1;
            for (int i = this.numberConsumers + 1; count < i; ++count) {
                logger.info("Starting consumer " + count + " of " + this.numberConsumers);
                KafkaProcessor consumer = new KafkaProcessor(this.consumerProcessor,
                        this.kafkaConsumerFactory, this.consumerProp, this.topicName, this.numberOfConsumerThreads);
                logger.info("Starting consumer with id=" + this.processorId);
                exec.submit(consumer);
                consumers.add(consumer);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        logger.info("Starting shutdown of kafka processors");
        for (KafkaProcessor consumer : this.getConsumers()) {
            consumer.getStopExecution().set(true);
            logger.info("Stopping consumer with id=" + this.processorId);
        }
        this.exec.shutdown();
        this.exec.awaitTermination(Long.parseLong(awaitTerminationTimeout), TimeUnit.SECONDS);
        this.exec = Executors.newFixedThreadPool(this.numberConsumers);
        this.getConsumers().clear();
        logger.info("Finished shutdown of kafka processors");
    }
}
