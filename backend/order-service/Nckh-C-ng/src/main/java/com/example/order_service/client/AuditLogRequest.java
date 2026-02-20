package com.example.order_service.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {
    private String sourceService;
    private String actionType;
    private String entityName;
    private String entityId;
    private String userId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
}
