package com.levi.wholesale.app;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@ComponentScan(basePackages = "com.levi")
@EnableJpaRepositories("com.levi.common.repository")
@EnableRetry
@EntityScan("com.levi.common.model")
@EnableBatchProcessing
public class OrderErrorProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderErrorProducerApplication.class, args);
    }
}
