package com.tokenmigration.app.entity;

import com.tokenmigration.app.enums.MigrateFrom;
import com.tokenmigration.app.enums.MigrationStatus;
import com.tokenmigration.app.enums.OperationStatus;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


@Document(collection = "migration-operations")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class MigrationOperationsEntity {

    @Id
    @Indexed
    @Field("migrationReference")
    private String migrationReference;

    @Field("tenantReference")
    private String tenantReference;

    @Indexed
    @Field("merchantReference")
    private String merchantReference;

    @Field("outletReference")
    private String outletReference;

    @Field("mid")
    private String mid;

    @Field("tokenGroup")
    private String tokenGroup;

    @Field("migrateFrom")
    private MigrateFrom migrateFrom;

    @Field("migrationStatus")
    private MigrationStatus migrationStatus;

    @Field("operationStatus")
    private OperationStatus operationStatus;

    @Field("numberOfRecords")
    private Integer numberOfRecords;

    @Field("publicUrl")
    private String publicUrl;

    @Field("startedBy")
    private String startedBy;

    @Field("createdAt")
    private Instant createdAt;

    @Field("finishedAt")
    private Instant finishedAt;

    @Field("startedAt")
    private Instant startedAt;

    @Field("editable")
    private boolean editable;

    @Field("deletable")
    private boolean deletable;

}
