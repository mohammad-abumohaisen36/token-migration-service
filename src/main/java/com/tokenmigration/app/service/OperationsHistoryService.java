package com.tokenmigration.app.service;

import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.model.response.MigrationOperationResponse;
import com.tokenmigration.app.model.reuest.MigrationRequest;

import java.util.List;
import java.util.Optional;

public interface OperationsHistoryService {

    MigrationOperationResponse createOrUpdate(MigrationRequest migrationRequest);


    Optional<OperationMigrationDto> findById(String id);

    List<OperationMigrationDto> findAllByTenantReference(String tenantRef);

    List<OperationMigrationDto> findAllByMid(String mid);

    List<OperationMigrationDto> findAllByMerchantRefAndTenantRef(String merchantRef, String tenantRef);
    int read(byte[] file,String migrationId);
}
