package com.tokenmigration.app.repository;

import com.tokenmigration.app.entity.MigrationOperationsEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationsHistoryRepository extends MongoRepository<MigrationOperationsEntity, String> {

    @Query("{'migrationReference' : ?0}")
    Optional<MigrationOperationsEntity> findById(String id);

    @Query("{}")
    List<MigrationOperationsEntity> findAll();

    @Query("{'tenantReference' : ?0}")
    List<MigrationOperationsEntity> findAllByTenantReference(String tenantRef);

    @Query("{'mid' : ?0}")
    List<MigrationOperationsEntity> findAllByMid(String mid);

    @Query("{ 'merchantReference' : ?0, 'tenantReference' : ?1 }")
    List<MigrationOperationsEntity> findAllByMerchantRefAndTenantRef(String merchantRef, String tenantRef);
}
