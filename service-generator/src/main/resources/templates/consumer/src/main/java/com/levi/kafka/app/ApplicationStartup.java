package com.levi.kafka.app;

import com.levi.kafka.app.consumer.ConsumerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class ApplicationStartup implements ApplicationRunner {

    static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    private ConsumerController consumerController;

    public ConsumerController getConsumerController() {
        return consumerController;
    }

    public void setConsumerController(ConsumerController consumerController) {
        this.consumerController = consumerController;
    }

    public ApplicationStartup(ConsumerController consumerController) {
        this.consumerController = consumerController;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.consumerController.start();
        logger.info("Initial startup - starting consumers");
    }
}
