package com.tokenmigration.app.repository.redis;



import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;

import java.util.Optional;

public interface MigrationRepository {

    BaseMigrationRedisEntity save(BaseMigrationRedisEntity entity);

    Optional<BaseMigrationRedisEntity> findById(String id);

    void deleteById(String id);

    Iterable<BaseMigrationRedisEntity> findAll();

     boolean existsById(String id);
}