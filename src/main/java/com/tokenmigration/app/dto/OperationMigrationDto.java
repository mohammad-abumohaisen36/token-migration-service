package com.tokenmigration.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokenmigration.app.enums.MigrateFrom;
import com.tokenmigration.app.enums.MigrationStatus;
import com.tokenmigration.app.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationMigrationDto {

    @JsonProperty("migrationReference")
    private String migrationReference;

    @JsonProperty("tenantReference")
    private String tenantReference;

    @JsonProperty("outletReference")
    private String outletReference;

    @JsonProperty("merchantReference")
    private String merchantReference;

    @JsonProperty("mid")
    private String mid;

    @JsonProperty("tokenGroup")
    private String tokenGroup;

    @JsonProperty("migrateFrom")
    private MigrateFrom migrateFrom;

    @JsonProperty("migrationStatus")
    private MigrationStatus migrationStatus;

    @JsonProperty("operationStatus")
    private OperationStatus operationStatus;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("startedBy")
    private String startedBy;

    @JsonProperty("finishedAt")
    private Instant finishedAt;

    @JsonProperty("startedAt")
    private Instant startedAt;

    @JsonProperty("numberOfRecords")
    private Integer numberOfRecords;

    @JsonProperty("publicUrl")
    private String publicUrl;

    @JsonProperty("editable")
    private boolean editable = true;

    @JsonProperty("deletable")
    private boolean deletable = true;
}
