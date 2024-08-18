package com.tokenmigration.app.repository;


import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.entity.MigrationOperationsEntity;

public interface OperationsRepository {

    MigrationOperationsEntity createOrUpdate(OperationMigrationDto operationMigrationDto);

     boolean existsByMigrationReference(String migrationReference);

}
