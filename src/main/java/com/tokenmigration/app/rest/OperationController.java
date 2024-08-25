package com.tokenmigration.app.rest;


import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.model.response.MigrationOperationResponse;
import com.tokenmigration.app.model.reuest.MigrationRequest;
import com.tokenmigration.app.service.OperationsHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/migration/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationsHistoryService operationsHistoryService;


    @PostMapping("/operation")
    public ResponseEntity<MigrationOperationResponse> addOperation(@Valid @RequestBody final MigrationRequest migrationRequest) {
        return ResponseEntity.ok(operationsHistoryService.createOrUpdate(migrationRequest));
    }

    @GetMapping("/tenants/{tenantReference}")
    public ResponseEntity<?> getAllOperationByTenantReference(@PathVariable String tenantReference) {
        List<OperationMigrationDto> operations = operationsHistoryService.findAllByTenantReference(tenantReference);
        return ResponseEntity.ok(operations);
    }

}