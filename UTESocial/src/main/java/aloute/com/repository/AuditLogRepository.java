package aloute.com.repository;

import aloute.com.entity.AuditLogs; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogs, Integer> 
{   
    @Query("SELECT DISTINCT al FROM AuditLogs al LEFT JOIN FETCH al.user ORDER BY al.createdAt DESC")
    Page<AuditLogs> findAllWithUserOrderByCreatedAtDesc(Pageable pageable);
}