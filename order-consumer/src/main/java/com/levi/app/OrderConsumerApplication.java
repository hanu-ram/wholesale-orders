package com.levi.app;

import com.levi.order.consumer.InitOrderConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("java:S1118")
@SpringBootApplication
@ComponentScan(basePackages = "com.levi")
@EnableJpaRepositories("com.levi.common.repository")
@EnableRetry
@EntityScan("com.levi.common.model")
@EnableScheduling
public class OrderConsumerApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    //Added this flag to prevent consumer run when running unit
    @Value("${run.order.consumer:true}")
    private boolean runConsumer;

    public static void main(String[] args) {
        SpringApplication.run(OrderConsumerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (runConsumer) {
            InitOrderConsumer consumer = context.getBean(InitOrderConsumer.class);
            consumer.initConsumer();
        }
    }
}
