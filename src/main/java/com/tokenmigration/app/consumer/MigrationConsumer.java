package com.tokenmigration.app.consumer;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;
import com.tokenmigration.app.enums.MigrateFrom;

import com.tokenmigration.app.service.impl.RedisEntityService;
import com.tokenmigration.app.service.impl.records.CsvRecord;
import com.tokenmigration.app.service.impl.records.Record;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.redisson.api.RLock;
import org.redisson.api.RMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class MigrationConsumer {

    public static final String MIGRATION_RECORD = "migration_record";
    private static final String FINAL_EXECUTION_LOCK_KEY = "finalExecutionLockKey";
    private static final String FINAL_ACTION_DONE_KEY = "finalActionDone";

    private final RedisEntityService redisEntityService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public MigrationConsumer(RedisEntityService redisEntityService) {
        this.redisEntityService = redisEntityService;

    }

    @KafkaListener(topics = "token-migration-data", containerFactory = "batchFactory")
    public void listenBatch(ConsumerRecords<String, Record> messageConsumerRecords) {

        CompletableFuture.allOf(StreamSupport.stream(messageConsumerRecords.spliterator(), false)
                .map(record -> CompletableFuture.runAsync(() -> processRecord(record), executorService))
                .toArray(CompletableFuture[]::new));
    }


    private void processRecord(ConsumerRecord<String, Record> record) {

        //CsvRecord value = record.value();
        if (record.value() instanceof CsvRecord value) {
            System.out.println(value.getMigrationId());
            BaseMigrationRedisEntity entity = new BaseMigrationRedisEntity();
            entity.setId(String.valueOf(value.getId()));
            entity.setMigrateFrom(MigrateFrom.CYBER_SOURCE);
            entity.setMid(value.getCode());

            // here we call our strategy and services
            redisEntityService.createOrUpdate(entity);
            String migrationId = value.getMigrationId();
            decrementRecordCount(migrationId);
            if (preformLastAction(migrationId)) {
                acquireLockAndPerformFinalAction(migrationId);
            }
        }
    }

    private boolean preformLastAction(String migrationId) {
        return getTotalRecordCount(migrationId) == 0 && redisEntityService.getMapCache(MIGRATION_RECORD).size() != 0;
    }

    private void acquireLockAndPerformFinalAction(String migrationId) {
        RLock lock = redisEntityService.getLock(FINAL_EXECUTION_LOCK_KEY);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (!isFinalActionPerformed(migrationId)) {
                    System.out.println("All batches processed. Performing final action...");
                    performFinalAction();
                    // we can  not remove the map for migrationId cuz we may have another threads that check if the action is done
                    redisEntityService.putInCache(migrationId, migrationId, -1, 1, TimeUnit.HOURS);
                    redisEntityService.putInCache(migrationId, FINAL_ACTION_DONE_KEY, true, 1, TimeUnit.HOURS);

                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void performFinalAction() {
        // here we to the clean up , save to database, send and upload the file ,send webhook
        System.out.println("Performing final action...");
        writeRecordsToCsv(redisEntityService.findAll());
    }

    private void writeRecordsToCsv(Stream<BaseMigrationRedisEntity> all) {


        String fileName = "/Users/mohammadabumohaisen/Documents/mohammad" + System.currentTimeMillis() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write CSV header
            writer.write("id,migrateFrom,mid\n");
            List<BaseMigrationRedisEntity> sortedRecords = all
                    .sorted((e1, e2) -> {
                        // Convert id to Integer and compare
                        Integer id1 = Integer.parseInt(e1.getId());
                        Integer id2 = Integer.parseInt(e2.getId());
                        return id1.compareTo(id2);
                    })
                    .toList();
            // Write CSV data
            for (BaseMigrationRedisEntity entity : sortedRecords) {
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


    private void decrementRecordCount(String migrationId) {
        RMap<String, Integer> totalRecordsMap = redisEntityService.getMapCache(migrationId);
        totalRecordsMap.addAndGet(migrationId, -1);
    }

    private int getTotalRecordCount(String migrationId) {
        return (int) redisEntityService.getMapCache(migrationId).get(migrationId);
    }

    private boolean isFinalActionPerformed(String migrationId) {
        return Boolean.TRUE.equals(redisEntityService.getMapCache(migrationId).get(FINAL_ACTION_DONE_KEY));
    }
}