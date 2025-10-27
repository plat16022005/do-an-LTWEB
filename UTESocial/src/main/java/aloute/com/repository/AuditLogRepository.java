package aloute.com.repository;

import aloute.com.entity.AuditLogs; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; 

import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.time.LocalTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogs, Integer> 
{   
    @Query("SELECT DISTINCT al FROM AuditLogs al LEFT JOIN FETCH al.user ORDER BY al.createdAt DESC")
    Page<AuditLogs> findAllWithUserOrderByCreatedAtDesc(Pageable pageable);
    
    
    //Lấy log và tìm kiếm, lọc log
    @Query("SELECT DISTINCT al FROM AuditLogs al LEFT JOIN FETCH al.user u WHERE " + // Luôn fetch User
            "(:keyword IS NULL OR :keyword = '' OR LOWER(al.details) LIKE LOWER(CONCAT('%', :keyword, '%')) OR (u IS NOT NULL AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.nameUser) LIKE LOWER(CONCAT('%', :keyword, '%'))))) " + // Tìm keyword trong details hoặc user info (nếu user tồn tại)
            "AND (:action IS NULL OR :action = '' OR al.action = :action) " + // Lọc theo action nếu có
            "AND (CAST(:startDate AS date) IS NULL OR al.createdAt >= :startDateTime) " + // Lọc theo ngày bắt đầu
            "AND (CAST(:endDate AS date) IS NULL OR al.createdAt <= :endDateTime) ") // Lọc theo ngày kết thúc
         
     Page<AuditLogs> findLogsWithFilters(
             @Param("keyword") String keyword,
             @Param("action") String action,
             @Param("startDate") LocalDate startDate, 
             @Param("endDate") LocalDate endDate,     
             @Param("startDateTime") LocalDateTime startDateTime, 
             @Param("endDateTime") LocalDateTime endDateTime,     
             Pageable pageable
     );

     
     default Page<AuditLogs> findLogsWithFilters(String keyword, String action, LocalDate startDate, LocalDate endDate, Pageable pageable) {
         LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
         LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
         return findLogsWithFilters(keyword, action, startDate, endDate, startDateTime, endDateTime, pageable);
     }
}