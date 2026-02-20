package com.example.audit.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.audit.repository.AuditRepository;
import com.example.audit.dto.AuditLogRequest;
import com.example.audit.entity.ActionType;
import com.example.audit.entity.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Async
    public void saveAuditLogAsync(AuditLogRequest request) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .sourceService(request.getSourceService())
                    .actionType(ActionType.valueOf(request.getActionType()))
                    .entityName(request.getEntityName())
                    .entityId(request.getEntityId())
                    .userId(request.getUserId())
                    .oldValue(request.getOldValue())
                    .newValue(request.getNewValue())
                    .ipAddress(request.getIpAddress())
                    .userAgent(request.getUserAgent())
                    .build();

            if (auditLog != null) {
                auditLogRepository.save(auditLog);
                logger.info("Audit log saved: {} - {} - {}",
                        request.getSourceService(), request.getActionType(), request.getEntityName());
            }
        } catch (Exception e) {
            logger.error("Failed to save audit log for entity: {}",
                    request.getEntityName(), e);
        }
    }

    public Page<AuditLog> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findAll(pageable);
    }

    public AuditLog getAuditLogById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    public Page<AuditLog> getAuditLogsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<AuditLog> getAuditLogsByEntity(String entityName, String entityId,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByEntityNameAndEntityIdOrderByCreatedAtDesc(
                entityName, entityId, pageable);
    }

    public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime from, LocalDateTime to,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByCreatedAtBetween(from, to, pageable);
    }

    public Page<AuditLog> searchAuditLogs(String actionType, String entityName,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (actionType != null && !actionType.trim().isEmpty()) {
            try {
                // Convert to uppercase and validate before conversion
                String upperActionType = actionType.trim().toUpperCase();

                // Additional validation: ensure it's a valid enum value
                try {
                    ActionType type = ActionType.valueOf(upperActionType);

                    // If entityName is provided, use it; otherwise search by actionType only
                    if (entityName != null && !entityName.trim().isEmpty()) {
                        return auditLogRepository.findByActionTypeAndEntityName(type, entityName, pageable);
                    } else {
                        return auditLogRepository.findByActionType(type, pageable);
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid action type attempted: {}", actionType);
                    // Return empty result instead of falling back to all logs
                    return Page.empty(pageable);
                }
            } catch (Exception e) {
                logger.error("Error searching audit logs: {}", e.getMessage());
                return Page.empty(pageable);
            }
        }

        // If no actionType provided but entityName is provided
        if (entityName != null && !entityName.trim().isEmpty()) {
            return auditLogRepository.findByEntityNameOrderByCreatedAtDesc(entityName, pageable);
        }

        return getAuditLogs(page, size);
    }
}