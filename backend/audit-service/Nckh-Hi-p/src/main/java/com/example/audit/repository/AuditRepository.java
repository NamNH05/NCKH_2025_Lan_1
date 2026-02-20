package com.example.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.audit.entity.AuditLog;
import com.example.audit.entity.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;


@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

    // Tìm lịch sử của 1 bản ghi cụ thể (Ví dụ: Xem ai đã sửa Order ID = 10)
    Page<AuditLog> findByEntityNameAndEntityIdOrderByCreatedAtDesc(
        String entityName, String entityId, Pageable pageable
    );

    // Tìm tất cả hành động của 1 người dùng
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Tra cứu log trong một khoảng thời gian (Quan trọng cho báo cáo/kiểm toán)
    Page<AuditLog> findByCreatedAtBetween(
        LocalDateTime from, LocalDateTime to, Pageable pageable
    );

    // Lọc theo loại hành động (Ví dụ: Chỉ xem các lệnh DELETE)
    Page<AuditLog> findByActionTypeAndEntityName(
        ActionType actionType, String entityName, Pageable pageable
    );
    
    // Lọc theo loại hành động (để handle trường hợp entityName = null)
    Page<AuditLog> findByActionType(ActionType actionType, Pageable pageable);
    
    // Lọc theo entity name
    Page<AuditLog> findByEntityNameOrderByCreatedAtDesc(String entityName, Pageable pageable);
    
    // Get all with pagination
    Page<AuditLog> findAll(Pageable pageable);
}