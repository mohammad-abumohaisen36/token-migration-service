package com.tokenmigration.app.repository.mongo.impl;


import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.entity.mongo.MigrationOperationsEntity;
import com.tokenmigration.app.repository.mongo.OperationsRepository;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import org.bson.Document;

import org.springframework.data.mongodb.core.query.Query;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class OperationsRepositoryImpl implements OperationsRepository {
    private final MongoTemplate mongoTemplate;


    public OperationsRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public MigrationOperationsEntity createOrUpdate(OperationMigrationDto operationMigrationDto) {

        boolean exists = existsByMigrationReference(operationMigrationDto.getMigrationReference());

        if (!exists) {
            operationMigrationDto.setMigrationReference(UUID.randomUUID().toString());
        }

        Document doc = new Document();
        mongoTemplate.getConverter().write(operationMigrationDto, doc);

        Update update = Update.fromDocument(doc);
        Query query = getQueryMongoId(operationMigrationDto);
        mongoTemplate.upsert(query, update, MigrationOperationsEntity.class);
        return mongoTemplate.findOne(query, MigrationOperationsEntity.class);
    }

    @Override
    public boolean existsByMigrationReference(String migrationReference) {
        Query query = query(where("migrationReference").is(migrationReference));
        return mongoTemplate.exists(query, MigrationOperationsEntity.class);
    }

    private Query getQueryMongoId(OperationMigrationDto operationMigrationDto) {
        return query(where("migrationReference").is(operationMigrationDto.getMigrationReference()));
    }


}
