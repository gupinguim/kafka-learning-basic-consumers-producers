package com.gugu.kafka.producer;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Component
@Log
public class KafkaProducerFirstTopic {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${kafka.topic}")
    private String TOPIC;

    public void sendMessage(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully");
            } else {
                log.severe("Error sending message: " + ex.getMessage());
            }
        });
    }

    public void close() {
        kafkaTemplate.flush();
    }
}