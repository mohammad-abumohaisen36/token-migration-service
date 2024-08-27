package com.tokenmigration.app.entity.redis;


import com.tokenmigration.app.enums.MigrateFrom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public sealed class BaseMigrationRedisEntity permits SchemeMigrationEntity, TMMigrationEntity {

    private String id;
    private String migrationReference;
    private String tenantReference;
    private String merchantReference;
    private String outletReference;
    private String mid;
    private String tokenGroup;
    private MigrateFrom migrateFrom;

}
