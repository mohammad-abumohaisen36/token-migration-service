package com.tokenmigration.app.rest;


import com.tokenmigration.app.dto.OperationMigrationDto;
import com.tokenmigration.app.model.response.MigrationOperationResponse;
import com.tokenmigration.app.model.reuest.MigrationRequest;
import com.tokenmigration.app.service.OperationsHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/migration/api")

public class OperationsHistoryController {

    private final OperationsHistoryService operationsHistoryService;

    public OperationsHistoryController(OperationsHistoryService operationsHistoryService) {
        this.operationsHistoryService = operationsHistoryService;
    }

    @PostMapping("/operation")
    public ResponseEntity<MigrationOperationResponse> addOperation(@Valid @RequestBody final MigrationRequest migrationRequest) {
        return ResponseEntity.ok(operationsHistoryService.createOrUpdate(migrationRequest));
    }

    @GetMapping("/{tenantReference}")
    public ResponseEntity<?> getAllOperationByTenantReference(@PathVariable String tenantReference) {
        List<OperationMigrationDto> operations = operationsHistoryService.findAllByTenantReference(tenantReference);
        return ResponseEntity.ok(operations);
    }


    @PostMapping("/migrate")
    public ResponseEntity<String> validateAndMigrate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("migrationReference") String migrationReference) {

        return ResponseEntity.ok("File and data received successfully");
    }
}