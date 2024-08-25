package com.tokenmigration.app.repository.mongo;


import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.entity.mongo.MigrationOperationsEntity;

public interface OperationsRepository {

    MigrationOperationsEntity createOrUpdate(OperationMigrationDto operationMigrationDto);

     boolean existsByMigrationReference(String migrationReference);

}
