package com.tokenmigration.app.repository.redis;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;

import java.util.stream.Stream;

public interface MigrationRepository {

    BaseMigrationRedisEntity save(BaseMigrationRedisEntity entity);


    Stream<BaseMigrationRedisEntity> findAll();

}