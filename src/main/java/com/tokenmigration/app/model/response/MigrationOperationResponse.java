package com.tokenmigration.app.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokenmigration.app.enums.MigrationStatus;
import com.tokenmigration.app.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MigrationOperationResponse {

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
    @JsonProperty("migrationStatus")
    private MigrationStatus migrationStatus;
    @JsonProperty("operationStatus")
    private OperationStatus operationStatus;
}
