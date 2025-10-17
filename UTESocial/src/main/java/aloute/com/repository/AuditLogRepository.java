package aloute.com.repository;

import aloute.com.entity.AuditLogs; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogs, Integer> 
{
    // Tự động hỗ trợ phân trang khi dùng Pageable
    Page<AuditLogs> findAllByOrderByCreatedAtDesc(Pageable pageable);
}