# kafka-learning-basic-consumers-producers
A repository with working code based on the classes took on Mareek Course about Kafka


## Intro

So basically the idea here is that you are able to install and Setup Kafka either in a Linux System or maybe you are using WSL 2 just like I did.

In any way, the approach I took was trying in some of the code try to use Spring Kafka instead of vanilla kakfa consumer 'cause sometimes your project is already a spring boot application so why not add up for that?

## How to Setup

The main part of the projects are configured on the <project>/src/main/resources/application.properties file.

There you have to setup basically 

spring.kafka.bootstrap-servers=localhost:9092 --> Setup the URL for your Kafka Broker
spring.kafka.consumer.group-id=gugu-group --> Give a name for your consumer group
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer --> IF you want to try to use other Serializers go ahead but I only did for String
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer


There are some hardcodes however I did have not time to properly setup an alternative so here they are

- kafka-consumer-opensearch: it expects you have a a topic named `wikimedia-topic` and it will try to subscribe for that with a default consumer group. It also expects for you to setup a local OpenSearch, configura that on the application resources and it should be a http local connection
- my-first-kafka-producer: it is all harcoded but all is configured on the main method, using vanilla kafka component for a producer, without spring. Should be for first contacgt with kafka only
- my-kafka-producer-wikimedia: has no hardcodes and it only needs for a local kafka to run. It will create the wikimedia topic if you don't set one before (and if you enable the permission for auto creation of topics of course)


## Stack

Java openjdk 21.0.6 2025-01-21
Apache Maven 3.6.3
WSL 2
kafka: 3.0.0
Docker Compose and Docker installed on Windows using WSL2 Linux to set up containers(Docker version 27.5.1, build 9f9e405) --> only used to setup the Open Search

## How to run

Get your local kafka UP

Run the producer to get some messages (you can either run through an IDE or try to go for a java run with spring)

Start the open search

Then run the consumer!