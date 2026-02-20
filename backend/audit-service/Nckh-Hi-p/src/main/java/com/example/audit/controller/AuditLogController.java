package com.example.audit.controller;

import com.example.audit.dto.AuditLogRequest;
import com.example.audit.service.AuditService;
import com.example.audit.entity.AuditLog;
import com.example.audit.util.InputValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditService auditService;
    private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);

    @PostMapping
    public ResponseEntity<Void> createAuditLog(@RequestBody @Valid AuditLogRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        auditService.saveAuditLogAsync(request); 
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Validate page parameters
        if (!InputValidator.isValidPageNumber(page)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page number. Must be >= 0"));
        }
        
        if (!InputValidator.isValidPageSize(size)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page size. Must be between 1 and 100"));
        }
        
        return ResponseEntity.ok(auditService.getAuditLogs(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuditLogById(@PathVariable Long id) {
        AuditLog log = auditService.getAuditLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAuditLogsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Validate page parameters
        if (!InputValidator.isValidPageNumber(page)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page number. Must be >= 0"));
        }
        
        if (!InputValidator.isValidPageSize(size)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page size. Must be between 1 and 100"));
        }
        
        return ResponseEntity.ok(auditService.getAuditLogsByUser(userId, page, size));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    public ResponseEntity<?> getAuditLogsByEntity(
            @PathVariable String entityName,
            @PathVariable String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Validate page parameters
        if (!InputValidator.isValidPageNumber(page)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page number. Must be >= 0"));
        }
        
        if (!InputValidator.isValidPageSize(size)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page size. Must be between 1 and 100"));
        }
        
        // Validate entityName format
        if (!InputValidator.isValidEntityName(entityName)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid entity name format"));
        }
        
        return ResponseEntity.ok(
            auditService.getAuditLogsByEntity(entityName, entityId, page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAuditLogs(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Validate page parameters
        if (!InputValidator.isValidPageNumber(page)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page number. Must be >= 0"));
        }
        
        if (!InputValidator.isValidPageSize(size)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page size. Must be between 1 and 100"));
        }
        
        // Validate actionType if provided
        if (actionType != null && !actionType.trim().isEmpty()) {
            if (!InputValidator.isValidActionType(actionType)) {
                logger.warn("Invalid actionType attempted: {}", actionType);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid action type. Allowed values: CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT, IMPORT"));
            }
        }
        
        // Validate entityName if provided
        if (entityName != null && !entityName.trim().isEmpty()) {
            if (!InputValidator.isValidEntityName(entityName)) {
                logger.warn("Invalid entityName attempted: {}", entityName);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid entity name. Must be alphanumeric with underscores/dashes"));
            }
        }
        
        return ResponseEntity.ok(
            auditService.searchAuditLogs(actionType, entityName, page, size));
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getAuditLogsByDateRange(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Validate page parameters
        if (!InputValidator.isValidPageNumber(page)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page number. Must be >= 0"));
        }
        
        if (!InputValidator.isValidPageSize(size)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid page size. Must be between 1 and 100"));
        }
        
        // Validate and parse date strings
        LocalDateTime fromDate = InputValidator.validateAndParseDate(from);
        LocalDateTime toDate = InputValidator.validateAndParseDate(to);
        
        if (fromDate == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid 'from' date format. Use: yyyy-MM-dd HH:mm:ss"));
        }
        
        if (toDate == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid 'to' date format. Use: yyyy-MM-dd HH:mm:ss"));
        }
        
        // Validate that 'from' is before 'to'
        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid date range. 'from' date must be before 'to' date"));
        }
        
        return ResponseEntity.ok(
            auditService.getAuditLogsByDateRange(fromDate, toDate, page, size));
    }
}
