package com.tokenmigration.app.service;

public interface MigrationService {
    int validateAndMigrate(byte[] byteArray, String migrationReference);
}
