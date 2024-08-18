package com.tokenmigration.app.model.reuest;

import com.fasterxml.jackson.annotation.*;
import com.tokenmigration.app.enums.MigrateFrom;
import com.tokenmigration.app.enums.MigrationStatus;
import com.tokenmigration.app.enums.OperationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MigrationRequest implements Serializable {


    @NotBlank
    @JsonProperty("tenantReference")
    private String tenantReference;

    @NotBlank
    @JsonProperty("outletReference")
    private String outletReference;

    @JsonProperty("migrationReference")
    private String migrationReference;

    @NotBlank
    @JsonProperty("merchantReference")
    private String merchantReference;

    @NotBlank
    @JsonProperty("mid")
    private String mid;

    @NotNull
    @JsonProperty("migrationType")
    private MigrateFrom migrateFrom;

    @NotBlank
    @JsonProperty("tokenGroup")
    private String tokenGroup;

    @JsonIgnore
    private MigrationStatus migrationStatus = MigrationStatus.PENDING;

    @JsonIgnore
    private OperationStatus operationStatus = OperationStatus.INACTIVE;


}