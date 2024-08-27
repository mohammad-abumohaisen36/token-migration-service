package com.tokenmigration.app.config;



import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;


import java.util.HashMap;;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Value("${migration-service-consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${migration-service-consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${migration-service-consumer.value-deserializer}")
    private String valueDeserializer;

    @Value("${migration-service-consumer.group-id}")
    private String groupId;

    @Value("${migration-service-consumer.auto-offset-reset}")
    private String offsetReset;

    @Value("${migration-service-consumer.auto-offset-commit}")
    private Boolean enableAutoCommit;


    @Bean
    public ConsumerFactory<String, Record> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "2000");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "3000");
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "300");
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, String.valueOf(5 * 1024 * 1024));
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "20971520");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(properties);
    }
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Record>> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Record> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

}
