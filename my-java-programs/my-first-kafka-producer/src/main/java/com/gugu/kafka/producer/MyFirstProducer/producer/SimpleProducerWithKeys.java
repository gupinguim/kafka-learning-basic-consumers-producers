package com.gugu.kafka.producer.MyFirstProducer.producer;

import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimpleProducerWithKeys {

    public static void main(String[] args) {

        log.info("Iniciando as propriedades de produtor");
        Properties props = new Properties();
        // propriedades de conexão
        props.setProperty("bootstrap.servers", "localhost:9092");
        // propriedades de serialização
        props.setProperty("key.serializer", StringSerializer.class.getName());
        props.setProperty("value.serializer", StringSerializer.class.getName());

        log.info("Criando o produtor");
        KafkaProducer<String, String> mySimpleProducer = new KafkaProducer<>(props);

        log.info("Criando a mensagem para o topico");


        String topic = "first-topic";
        String[] keys = { "andreza", "maria", "pedro", "augusto" };

        log.info("Enviando a mensagem");
        for (int i = 0; i < 3; i++) {
            
            for (String key : keys) {
                String dataHoraAtual = (new Date()).toString();

                ProducerRecord<String, String> message = new ProducerRecord<String, String>(topic, key,
                        key + " - msg do Java  " + dataHoraAtual);

                mySimpleProducer.send(message, (metadata, exception) -> {
                    if (exception != null) {
                        log.error("Erro ao enviar a mensagem", exception);
                    } else {
                        log.info("Mensagem enviada com sucesso para o topico: " + metadata.topic() + " na partição: "
                                + metadata.partition() + " para a chave: " + key);
                    }
                });
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        log.info("Flush e Close no Producer");
        mySimpleProducer.flush();

        mySimpleProducer.close();
        log.info("Fim do programa, cheque a mensagem");

    }

}
