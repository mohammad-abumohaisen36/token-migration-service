package com.tokenmigration.app.service.impl;

import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.entity.MigrationOperationsEntity;
import com.tokenmigration.app.mapper.OperationMigrationMapper;
import com.tokenmigration.app.model.response.MigrationOperationResponse;
import com.tokenmigration.app.model.reuest.MigrationRequest;
import com.tokenmigration.app.repository.OperationsHistoryRepository;
import com.tokenmigration.app.repository.OperationsRepository;
import com.tokenmigration.app.service.OperationsHistoryService;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class OperationsHistoryServiceImpl implements OperationsHistoryService {
    private final OperationMigrationMapper operationMigrationMapper = OperationMigrationMapper.INSTANCE;
    private final OperationsHistoryRepository operationsHistoryRepository;
    private final OperationsRepository operationsRepository;


    public OperationsHistoryServiceImpl(OperationsHistoryRepository operationsHistoryRepository, OperationsRepository operationsRepository) {
        this.operationsHistoryRepository = operationsHistoryRepository;
        this.operationsRepository = operationsRepository;
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

    private List<OperationMigrationDto> findAndMapToDtoList(@NotNull List<@NotNull MigrationOperationsEntity> entityList) {
        return Optional.of(entityList)
                .filter(entities -> !entities.isEmpty())
                .map(operationMigrationMapper::mapToDto)
                .orElse(Collections.emptyList());
    }
}
