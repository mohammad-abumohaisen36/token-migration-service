package com.tokenmigration.app.service.impl;

import com.tokenmigration.app.entity.redis.BaseMigrationRedisEntity;
import com.tokenmigration.app.repository.redis.MigrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisEntityService {

    private final MigrationRepository redisRepository;


    public BaseMigrationRedisEntity createOrUpdate(BaseMigrationRedisEntity entity) {
        return redisRepository.save(entity);
    }

    public Optional<BaseMigrationRedisEntity> read(String id) {
        return redisRepository.findById(id);
    }

    public void delete(String id) {
        redisRepository.deleteById(id);
    }

    public boolean exists(String id) {
        return redisRepository.existsById(id);
    }

    public Iterable<BaseMigrationRedisEntity> findAll() {
        return redisRepository.findAll();
    }
}