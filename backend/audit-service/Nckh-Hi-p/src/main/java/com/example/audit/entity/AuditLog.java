package com.example.audit.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_entity", columnList = "entity_name, entity_id"),
        @Index(name = "idx_audit_logs_user", columnList = "user_id"),
        @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_seq")
    @SequenceGenerator(name = "audit_log_seq", sequenceName = "AUDIT_LOG_SEQ", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private ActionType actionType;

    @Column(name = "source_service", length = 50) 
    private String sourceService;

    @Column(name = "entity_name", nullable = false, length = 50)
    private String entityName;

    @Column(name = "entity_id", length = 50)
    private String entityId;

    @Lob // Trong Oracle, @Lob sẽ mapping với kiểu CLOB
    @Column(name = "old_value")
    private String oldValue;

    @Lob // CLOB dùng để lưu chuỗi JSON dài vượt quá 4000 ký tự
    @Column(name = "new_value")
    private String newValue;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}