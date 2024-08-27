package com.tokenmigration.app.config;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public KafkaProducer<String, ?> kafkaProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(32 * 1024)); // 32KB
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "100"); // 10ms
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, String.valueOf(64 * 1024 * 1024)); // 64MB
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "5");
        properties.setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "3000");
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, String.valueOf(2 * 1024 * 1024)); // 2MB


        return new KafkaProducer<>(properties);
    }

}
