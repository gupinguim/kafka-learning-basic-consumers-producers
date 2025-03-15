package com.gugu.kafka.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gugu.kafka.producer.KafkaProducerFirstTopic;
import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;

import lombok.extern.java.Log;;

@Component
@Log
public class WikimediaEventHandler implements BackgroundEventHandler {

    @Autowired
    private KafkaProducerFirstTopic kafkaProducer;

    @Override
    public void onClosed() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
        return; // "Unimplemented method 'onClosed'");
    }

    @Override
    public void onComment(String arg0) throws Exception {
        return; // "Unimplemented method 'onComment'");
    }

    @Override
    public void onError(Throwable exception) {
        log.severe("Error in Stream Reading: " + exception.getMessage());
    }

    @Override
    public void onMessage(String event, MessageEvent message) throws Exception {
        String actualMessage = message.getData();
        log.info("Event: " + event);

        if (kafkaProducer != null) {
            kafkaProducer.sendMessage(actualMessage);
        }

    }

    @Override
    public void onOpen() {

        return; // "Unimplemented method 'onOpen'");
    }

}
