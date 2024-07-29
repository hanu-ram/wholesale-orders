package com.levi.kafka.app;


import com.levi.kafka.app.consumer.ConsumerController;
import com.levi.kafka.app.util.KafkaConsumerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerApplication {

    @Bean
    public KafkaConsumerFactory getKafkaFactory() {
        return new KafkaConsumerFactory();
    }

    @Bean
    public ApplicationRunner getApplicationRunner(ConsumerController consumerController) {
        return (ApplicationRunner) (new ApplicationStartup(consumerController));
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
