package com.levi.kafka.app.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
public class KafkaProducer {

    @Autowired
    private KafkaPublisher kafkaPublisher;
    @Value("${kafka.producer.topic.name}")
    private String topicName;

    @PostMapping("/publish/{message}")
    public ResponseEntity<String> post(@PathVariable("message") final String message) throws InterruptedException {
        if (kafkaPublisher.publish(topicName, message)) {

            return ResponseEntity.ok("Published Successfully");

        } else {
            return ResponseEntity.badRequest().body("Publish Failed");
        }
    }

    public KafkaPublisher getKafkaPublisher() {
        return kafkaPublisher;
    }

    public void setKafkaPublisher(KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
