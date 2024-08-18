package com.tokenmigration.app.config;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;


@Configuration
public class KafkaProducerConfiguration {

    @Value("${migration-service-producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${migration-service-producer.key-serializer}")
    private String keySerializer;

    @Value("${migration-service-producer.value-serializer}")
    private String valueSerializer;


    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        properties.setProperty(JsonSerializer.ADD_TYPE_INFO_HEADERS, "*");

        return new KafkaProducer<>(properties);
    }

}
