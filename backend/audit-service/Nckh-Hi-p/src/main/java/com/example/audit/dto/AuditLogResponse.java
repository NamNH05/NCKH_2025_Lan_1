package com.example.audit.dto;
public record AuditLogResponse(
    Long id,
    String sourceService,   // Thêm từ DTO của bạn
    String actionType,      // CREATE, UPDATE...
    String entityName,      // User, Order...
    String entityId,        // ID của bản ghi bị sửa
    String userId,          // Ai làm
    String oldValue,        // Dữ liệu cũ (JSON)
    String newValue,        // Dữ liệu mới (JSON)
    String ipAddress,       // Địa chỉ IP
    String createdAt        // "07/01/2026 20:50"
) {}