package com.tokenmigration.app.repository.redis.impl;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;
import com.tokenmigration.app.repository.redis.MigrationRepository;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedissonMigrationRepository implements MigrationRepository {

    private final RedissonClient redissonClient;
    private static final String MAP_NAME = "migration_record";
    private static final long TTL_SECONDS = 4 * 60; // Example: 1 hour TTL
    private static final long MAX_IDLE_SECONDS = 2 * 60; // Example: 30 minutes max idle time


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
        mapCache.put(entity.getId(), entity, TTL_SECONDS, TimeUnit.SECONDS);
        return entity;
    }

    @Override
    public Optional<BaseMigrationRedisEntity> findById(String id) {
        RMap<String, BaseMigrationRedisEntity> map = getMapCache();
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public void deleteById(String id) {
        RMap<String, BaseMigrationRedisEntity> map = getMapCache();
        map.remove(id);
    }

    @Override
    public Iterable<BaseMigrationRedisEntity> findAll() {
        RMap<String, BaseMigrationRedisEntity> map = getMapCache();
        return map.values();
    }

    @Override
    public boolean existsById(String id) {
        RMap<String, BaseMigrationRedisEntity> map = redissonClient.getMap("migration_record");
        return map.containsKey(id);
    }
}