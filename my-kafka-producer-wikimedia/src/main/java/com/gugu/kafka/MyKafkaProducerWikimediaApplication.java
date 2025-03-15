package com.gugu.kafka;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.gugu.kafka.handlers.WikimediaEventHandler;
import com.launchdarkly.eventsource.ConnectStrategy;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventSource;

@SpringBootApplication
@ComponentScan(basePackages = "com.gugu.kafka")

public class MyKafkaProducerWikimediaApplication implements CommandLineRunner {
	@Autowired
	WikimediaEventHandler wikimediaEventHandler;

	public static void main(String[] args) {
		SpringApplication.run(MyKafkaProducerWikimediaApplication.class, args);
	}

	@Override
	public void run(String... args) throws InterruptedException {
		String url = "https://stream.wikimedia.org/v2/stream/recentchange";

		BackgroundEventSource.Builder builder = new BackgroundEventSource.Builder(
				wikimediaEventHandler,
				new EventSource.Builder(ConnectStrategy.http(URI.create(url)))
		);

		BackgroundEventSource bgEventSource = builder.build();

		bgEventSource.start();

		TimeUnit.MINUTES.sleep(10);
	}

}
