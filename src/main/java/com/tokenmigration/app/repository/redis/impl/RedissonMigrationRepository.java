package com.tokenmigration.app.repository.redis.impl;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;
import com.tokenmigration.app.repository.redis.MigrationRepository;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Repository
public class RedissonMigrationRepository implements MigrationRepository {

    private final RedissonClient redissonClient;
    private static final String MAP_NAME = "migration_record";

    @Autowired
    public RedissonMigrationRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private RMapCache<String, BaseMigrationRedisEntity> getMapCache() {
        return redissonClient.getMapCache(MAP_NAME);
    }

    @Override
    public BaseMigrationRedisEntity save(BaseMigrationRedisEntity entity) {
        RMapCache<String, BaseMigrationRedisEntity> mapCache = getMapCache();
        mapCache.put(entity.getId(), entity, 1, TimeUnit.HOURS);
        return entity;
    }

    @Override
    public Stream<BaseMigrationRedisEntity> findAll() {
        RMap<String, BaseMigrationRedisEntity> map = getMapCache();
        return map.values().stream();
    }


}