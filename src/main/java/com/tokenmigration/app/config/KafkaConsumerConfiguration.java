package com.tokenmigration.app.config;


import com.tokenmigration.app.service.impl.CsvRecord;
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


import java.util.HashMap;
;
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
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        // Basic Kafka consumer configuration
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Adjust offset reset behavior (e.g., "earliest" to consume from the beginning)
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset); // "earliest" or "latest"

        // Deserializer configuration
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);

        // Disable auto commit for manual commit and better control over message processing
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit); // false for manual commit

        // Customizing the poll interval to balance message consumption and processing
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000"); // 5 minutes

        // Increase the maximum number of records returned in a single poll to enhance throughput
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000"); // Set this as high as you can handle

        // Session timeout and heartbeat interval settings for consumer group management
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000"); // 10 seconds
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "3000"); // 3 seconds

        // Fetch size configuration to optimize network usage
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1"); // Minimum data to fetch in a single request
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "100"); // Reduce wait time for faster fetches

        // Fine-tune buffer size to improve network and I/O performance
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, String.valueOf(5 * 1024 * 1024)); // Increase to 20MB

        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "20971520");

        // Additional JSON Deserializer configuration
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CsvRecord.class.getName());

        return new DefaultKafkaConsumerFactory<>(properties);
    }
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }

}
