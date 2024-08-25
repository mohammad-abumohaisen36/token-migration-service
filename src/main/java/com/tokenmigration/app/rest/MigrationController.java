package com.tokenmigration.app.rest;


import com.tokenmigration.app.service.OperationsHistoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/migration")
@RequiredArgsConstructor
public class MigrationController {

    private final OperationsHistoryService operationsHistoryService;


    @PostMapping("/migrate/{migrationId}")
    public ResponseEntity<String> validateAndMigrate(
            @RequestParam("file") MultipartFile file,
            @PathVariable String migrationId) {
        try {

            int read = operationsHistoryService.read(file.getBytes(),migrationId);
            return ResponseEntity.ok("File and data received successfully :" + read);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the file");
        }

    }
}