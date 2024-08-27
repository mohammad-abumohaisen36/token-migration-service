package com.tokenmigration.app.service.impl;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;
import com.tokenmigration.app.repository.redis.MigrationRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RedisEntityService {

    private final MigrationRepository redisRepository;
    @Getter
    private final RedissonClient redissonClient;


    public void createOrUpdate(BaseMigrationRedisEntity entity) {
         redisRepository.save(entity);
    }

    public Stream<BaseMigrationRedisEntity> findAll() {
        return redisRepository.findAll();
    }

    public <K, V> RMapCache<K, V> getMapCache(String cacheKey) {
        return redissonClient.getMapCache(cacheKey);
    }


    public Stream<BaseMigrationRedisEntity> findAllByMigrationId(String migrationId) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    // General method to put values into Redis MapCache
    public <T> void putInCache(String cacheKey, String fieldKey, T value, long ttl, TimeUnit timeUnit) {
        redissonClient.getMapCache(cacheKey).put(fieldKey, value, ttl, timeUnit);
    }

    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }
}