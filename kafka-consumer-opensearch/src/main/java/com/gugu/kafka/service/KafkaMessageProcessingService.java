package com.gugu.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class KafkaMessageProcessingService {

    @Autowired
    private OpenSearchService openSearchService;

    public boolean processMessage(String message, String id) throws Exception {

        try {
            return openSearchService.sendMessage(message, id) ;
        } catch (Exception e) {
            log.severe(message);
            throw e;
        }
    }

    public void dispose() {
        openSearchService.dispose();
    }
}