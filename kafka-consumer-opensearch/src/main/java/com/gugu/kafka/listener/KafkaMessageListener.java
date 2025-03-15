package com.gugu.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.google.gson.JsonParser;
import com.gugu.kafka.service.KafkaMessageProcessingService;

import lombok.extern.java.Log;

@Component
@Log
public class KafkaMessageListener {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private KafkaMessageProcessingService messageProcessingService;

    @KafkaListener(topics = "wikimedia-topic", groupId = "gugu-group", containerFactory = "kafkaListenerContainerFactory", autoStartup = "true")
    public void listenTopicWikimedia(String message, Acknowledgment acknowledgment) {
        log.info("Recebemos uma mensagem, processando...");
        try {
            String id = extractId(message);

            boolean commit = messageProcessingService.processMessage(message, id);

            if (commit) {
                log.info("Commitando mensagem");
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.severe("Erro ao processar mensagem");
            messageProcessingService.dispose();
            SpringApplication.exit(applicationContext, () -> 1);
        }

    }

    private String extractId(String message) {
        return JsonParser.parseString(message).getAsJsonObject().get("meta").getAsJsonObject().get("id").getAsString();
    }
}