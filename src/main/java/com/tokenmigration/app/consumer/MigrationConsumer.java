package com.tokenmigration.app.consumer;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;
import com.tokenmigration.app.enums.MigrateFrom;
import com.tokenmigration.app.service.impl.CsvRecord;
import com.tokenmigration.app.service.impl.RedisEntityService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Headers;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class MigrationConsumer {

    private final RedisEntityService redisEntityService;
    private final RedissonClient redissonClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static final String BATCH_TRACKING_KEY = "batchTrackingKey";
    private static final String FINAL_EXECUTION_LOCK_KEY = "finalExecutionLockKey";

    public MigrationConsumer(RedisEntityService redisEntityService, RedissonClient redissonClient) {
        this.redisEntityService = redisEntityService;
        this.redissonClient = redissonClient;
    }

    @KafkaListener(topics = "token-migration-data", containerFactory = "batchFactory")
    public void listenBatch(ConsumerRecords<String, CsvRecord> messageConsumerRecords) {
        long startTime = System.currentTimeMillis();

        System.out.println("Received Message in Batch: " + messageConsumerRecords.count());

        RMap<String, Integer> batchTrackingMap = redissonClient.getMap(BATCH_TRACKING_KEY);

        incrementBatchCount(batchTrackingMap);

        CompletableFuture<?>[] futures = StreamSupport.stream(messageConsumerRecords.spliterator(), false)
                .map(record -> CompletableFuture.runAsync(() -> processRecord(record), executorService))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).thenRun(() -> {
            // Decrement the batch count after processing all records in the batch
            decrementBatchCount(batchTrackingMap);
            checkAndPerformFinalAction(messageConsumerRecords);
        }).join();

        System.out.println("Total Processing Time: " + (System.currentTimeMillis() - startTime));
    }

    private void processRecord(ConsumerRecord<String, CsvRecord> record) {
        CsvRecord value = record.value();
        BaseMigrationRedisEntity entity = new BaseMigrationRedisEntity();
        entity.setId(String.valueOf(value.getId()));
        entity.setMigrateFrom(MigrateFrom.CYBER_SOURCE);
        entity.setMid(value.getCode());

        redisEntityService.createOrUpdate(entity);
    }

    private void checkAndPerformFinalAction(ConsumerRecords<String, CsvRecord> records) {
        if (isLastBatch(records)) {
            acquireLockAndPerformFinalAction();
        }
    }

    private boolean isLastBatch(ConsumerRecords<String, CsvRecord> records) {
        for (ConsumerRecord<String, CsvRecord> record : records) {
            Headers headers = record.headers();
            if (headers != null && headers.lastHeader("isLastBatch") != null) {
                String headerValue = new String(headers.lastHeader("isLastBatch").value(), StandardCharsets.UTF_8);
                if ("true".equals(headerValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void acquireLockAndPerformFinalAction() {
        RLock lock = redissonClient.getLock(FINAL_EXECUTION_LOCK_KEY);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                System.out.println("All batches processed. Performing final action...");
                performFinalAction();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void performFinalAction() {
        System.out.println("Performing final action...");
        RMap<String, Integer> batchTrackingMap = redissonClient.getMap(BATCH_TRACKING_KEY);
         batchTrackingMap.remove("processingBatches");

        writeRecordsToCsv(redisEntityService.findAll());
    }

    private void writeRecordsToCsv(Iterable<BaseMigrationRedisEntity> all) {
        List<BaseMigrationRedisEntity> sortedList = StreamSupport.stream(all.spliterator(), false)
                .sorted((e1, e2) -> {
                    Integer id1 = parseId(e1.getId());
                    Integer id2 = parseId(e2.getId());
                    return Integer.compare(id1, id2);
                })
                .collect(Collectors.toList());

        String fileName = "/Users/mohammadabumohaisen/Documents/mohammad2.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write CSV header
            writer.write("id,migrateFrom,mid\n");

            // Write CSV data
            for (BaseMigrationRedisEntity entity : sortedList) {
                writer.write(String.format("%s,%s,%s\n",
                        entity.getId(),
                        entity.getMigrateFrom() != null ? entity.getMigrateFrom().name() : "",
                        entity.getMid()));
            }

            System.out.println("CSV file created at " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // or handle it as needed
        }
    }

    private void incrementBatchCount(RMap<String, Integer> batchTrackingMap) {
        batchTrackingMap.addAndGet("processingBatches", 1);
    }

    private void decrementBatchCount(RMap<String, Integer> batchTrackingMap) {
        batchTrackingMap.addAndGet("processingBatches", -1);
    }
}