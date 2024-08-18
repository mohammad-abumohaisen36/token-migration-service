package com.tokenmigration.app.service;

public interface MigrationService {
    void validateAndMigrate(String file, String migrationReference);
}
