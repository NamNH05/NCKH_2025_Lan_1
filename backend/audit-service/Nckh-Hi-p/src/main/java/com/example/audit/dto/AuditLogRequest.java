package com.example.audit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {
    @NotBlank(message = "Source service is required")
    private String sourceService;

    @NotBlank(message = "Action type is required")
    private String actionType;

    @NotBlank(message = "Entity name is required")
    private String entityName;

    @NotBlank(message = "Entity ID is required")
    private String entityId;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String oldValue;
    private String newValue;

    private String ipAddress;
    private String userAgent;
}