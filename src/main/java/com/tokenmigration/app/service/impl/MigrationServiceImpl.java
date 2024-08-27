package com.tokenmigration.app.service.impl;


import com.tokenmigration.app.file.parser.CsvParserBuilder;
import com.tokenmigration.app.repository.mongo.OperationsHistoryRepository;
import com.tokenmigration.app.repository.mongo.OperationsRepository;
import com.tokenmigration.app.service.MigrationService;

import com.tokenmigration.app.service.impl.records.CustomBeanListProcessor;
import com.tokenmigration.app.service.impl.records.CsvRecord;
import com.univocity.parsers.csv.CsvParser;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class MigrationServiceImpl implements MigrationService {
    private static final int BATCH_SIZE = 2000;
    private final OperationsHistoryRepository operationsHistoryRepository;
    private final OperationsRepository operationsRepository;
    private final KafkaProducer<String, CsvRecord> producer;
    private final RedisEntityService redisEntityService;


    public MigrationServiceImpl(OperationsHistoryRepository operationsHistoryRepository,
                                OperationsRepository operationsRepository, KafkaProducer<String, CsvRecord> producer,
                                RedisEntityService redisEntityService) {
        this.operationsHistoryRepository = operationsHistoryRepository;
        this.operationsRepository = operationsRepository;
        this.producer = producer;
        this.redisEntityService = redisEntityService;
    }

    @Override
    public int validateAndMigrate(byte[] byteArray, String migrationId) {
        //BeanListProcessor<CsvRecord> processor = new BeanListProcessor<>(CsvRecord.class);
        CustomBeanListProcessor<CsvRecord> processor = new CustomBeanListProcessor<>(CsvRecord.class, migrationId);
        CsvParser csvParser = CsvParserBuilder.builder()
                .headerExtractionEnabled(true)
                .ignoreLeadingWhitespaces(true)
                .ignoreTrailingWhitespaces(true)
                .skipEmptyLines(true)
                .processor(processor)
                .build().build();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
             Reader inputReader = new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8)) {

            long start = System.currentTimeMillis();
            csvParser.parse(inputReader);
            List<CsvRecord> csvRecords = processor.getBeans();
            System.out.println("Total objects: " + csvRecords.size());

            // Store the total record count in Redis
            redisEntityService.putInCache(migrationId, migrationId, csvRecords.size(), 1, TimeUnit.HOURS);
            redisEntityService.putInCache(migrationId, "finalActionDone", false, 1, TimeUnit.HOURS);

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            int totalRecords = csvRecords.size();

            if (totalRecords <= BATCH_SIZE) {
                futures.add(CompletableFuture.runAsync(() -> processBatch(csvRecords), executor));
            } else {
                // Process in regular batches
                int numberOfBatches = (totalRecords + BATCH_SIZE - 1) / BATCH_SIZE;
                for (int i = 0; i < numberOfBatches; i++) {
                    int startIndex = i * BATCH_SIZE;
                    int endIndex = Math.min(startIndex + BATCH_SIZE, totalRecords);
                    List<CsvRecord> batch = csvRecords.subList(startIndex, endIndex);
                    futures.add(CompletableFuture.runAsync(() -> processBatch(batch), executor));
                }
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long end = System.currentTimeMillis();
            return (int) (end - start);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return 0;
    }

    private void processBatch(List<CsvRecord> batch) {
        batch.forEach(record -> {

            ProducerRecord<String, CsvRecord> producerRecord = new ProducerRecord<>(
                    "token-migration-data", new Random().nextInt(10), UUID.randomUUID().toString(), record);

            producer.send(producerRecord);
        });
        System.out.println("Batch processed: " + batch.size());
    }
}
