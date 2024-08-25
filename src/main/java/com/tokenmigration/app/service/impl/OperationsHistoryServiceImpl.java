package com.tokenmigration.app.service.impl;

import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.entity.mongo.MigrationOperationsEntity;
import com.tokenmigration.app.mapper.OperationMigrationMapper;
import com.tokenmigration.app.model.response.MigrationOperationResponse;
import com.tokenmigration.app.model.reuest.MigrationRequest;
import com.tokenmigration.app.repository.mongo.OperationsHistoryRepository;
import com.tokenmigration.app.repository.mongo.OperationsRepository;
import com.tokenmigration.app.service.OperationsHistoryService;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class OperationsHistoryServiceImpl implements OperationsHistoryService {
    private static final int BATCH_SIZE = 1000;
    private final OperationMigrationMapper operationMigrationMapper = OperationMigrationMapper.INSTANCE;
    private final OperationsHistoryRepository operationsHistoryRepository;
    private final OperationsRepository operationsRepository;
    private final KafkaProducer<String, CsvRecord> producer;


    public OperationsHistoryServiceImpl(OperationsHistoryRepository operationsHistoryRepository,
                                        OperationsRepository operationsRepository, KafkaProducer<String, CsvRecord> producer) {
        this.operationsHistoryRepository = operationsHistoryRepository;
        this.operationsRepository = operationsRepository;
        this.producer = producer;
    }

    @Override
    public MigrationOperationResponse createOrUpdate(MigrationRequest request) {

        return operationMigrationMapper
                .mapToResponse(operationsRepository
                        .createOrUpdate(operationMigrationMapper
                                .mapToDto(request)
                        )
                );

    }

    @Override
    public Optional<OperationMigrationDto> findById(@NotNull String id) {
        return operationsHistoryRepository.findById(id)
                .map(operationMigrationMapper::mapToDto)
                .or(Optional::empty);
    }


    @Override
    public List<OperationMigrationDto> findAllByTenantReference(@NotNull String tenantRef) {
        return findAndMapToDtoList(operationsHistoryRepository.findAllByTenantReference(tenantRef));
    }

    @Override
    public List<OperationMigrationDto> findAllByMid(@NotNull String mid) {
        return findAndMapToDtoList(operationsHistoryRepository.findAllByMid(mid));
    }

    @Override
    public List<OperationMigrationDto> findAllByMerchantRefAndTenantRef(@NotNull String merchantRef, @NotNull String tenantRef) {
        return findAndMapToDtoList(operationsHistoryRepository.findAllByMerchantRefAndTenantRef(merchantRef, tenantRef));
    }

    @Override
    public int read(byte[] byteArray, String migrationId) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setIgnoreLeadingWhitespaces(true);
        settings.setIgnoreTrailingWhitespaces(true);
        settings.setSkipEmptyLines(true);

        BeanListProcessor<CsvRecord> rowProcessor = new BeanListProcessor<>(CsvRecord.class);
        settings.setProcessor(rowProcessor);

        CsvParser parser = new CsvParser(settings);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
             Reader inputReader = new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8)) {

            long start = System.currentTimeMillis();
            parser.parse(inputReader);
            List<CsvRecord> csvRecords = rowProcessor.getBeans();
            System.out.println("Total objects: " + csvRecords.size());

            AtomicInteger key = new AtomicInteger(1);
            int totalBatches = (int) Math.ceil((double) csvRecords.size() / BATCH_SIZE);

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < csvRecords.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, csvRecords.size());
                List<CsvRecord> batch = csvRecords.subList(i, end);

                CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processBatch(batch, key.getAndIncrement(), totalBatches, csvRecords.size()), executor
                );

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            long end = System.currentTimeMillis();
            System.out.println("Time taken: " + (end - start) + "ms");
            System.out.println("Number of records: " + csvRecords.size());
            return (int) (end - start);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return 0;
    }

    private void processBatch(List<CsvRecord> batch, int batchNumber, int totalBatches, int totalRecords) {
        boolean isLastBatch = batchNumber == totalBatches;
        String batchKey = "batch_" + batchNumber;

        batch.forEach(record -> {
            Headers headers = new RecordHeaders();
            if (isLastBatch && batch.get(batch.size() - 1) == record) {
                headers.add("isLastBatch", "true".getBytes(StandardCharsets.UTF_8));
            }

            ProducerRecord<String, CsvRecord> producerRecord = new ProducerRecord<>(
                    "token-migration-data", null, null, batchKey, record, headers
            );
            producer.send(producerRecord);
        });
    }
    private List<OperationMigrationDto> findAndMapToDtoList(@NotNull List<@NotNull MigrationOperationsEntity> entityList) {
        return Optional.of(entityList)
                .filter(entities -> !entities.isEmpty())
                .map(operationMigrationMapper::mapToDto)
                .orElse(Collections.emptyList());
    }



}
