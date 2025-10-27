package aloute.com.repository.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer> 
{
    long countByStatus(String status);
    List<Reports> findByPostAndReporter(Posts post, User reporter);
    List<Reports> findByReportedUserAndReporter(User reportedUser, User reporter);
    List<Reports> findByReportedUserAndReporterAndType(User reportedUser, User reporter, String type);
    List<Reports> findByStatus(String status);
    List<Reports> findByResolutionStatus(String resolutionStatus);
    
    
    @Query("SELECT r FROM Reports r " +
    	       "LEFT JOIN FETCH r.reporter " + // Lấy người tố cáo
    	       "LEFT JOIN FETCH r.reportedUser " + // Lấy người bị tố cáo
    	       "LEFT JOIN FETCH r.post p " + // Lấy bài viết 
    	       "LEFT JOIN FETCH p.user pu " + // Lấy người đăng bài viết 
    	       "LEFT JOIN FETCH p.attachments " + // Lấy tệp đính kèm của bài viết 
    	       "WHERE r.reportId = :reportId")
    	Optional<Reports> findByIdWithDetails(@Param("reportId") Integer reportId);
    
    
    //Lấy danh sách tố cáo, khiếu nại và tìm kiếm theo keyword, lọc theo cột
    @Query("SELECT DISTINCT r FROM Reports r " +
    	       "LEFT JOIN FETCH r.reporter " +
    	       "LEFT JOIN FETCH r.reportedUser " +
    	       "LEFT JOIN FETCH r.post p " + // Fetch post 
    	       "ORDER BY r.createdAt DESC") // Sắp xếp theo ngày tạo mới nhất lên đầu
    	List<Reports> findAllWithReporterAndReportedUserOrderByCreatedAtDesc();


	@Query("SELECT DISTINCT r FROM Reports r " +
            "LEFT JOIN FETCH r.reporter rep " +          // Fetch thông tin người dùng tố cáo
            "LEFT JOIN FETCH r.reportedUser rpu " +     // Fetch thông tin người dùng bị tố cáo
            "LEFT JOIN FETCH r.post p " +               // Fetch thông tin post 
            "WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(r.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rep.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rpu.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + // Search keyword in reason, reporter name, reported user name
            "AND (:type IS NULL OR :type = '' OR r.type = :type) " +                  // Lọc theo type
            "AND (:status IS NULL OR :status = '' OR r.status = :status) " +              // Lọc theo trạng thái
            "AND (:resolutionStatus IS NULL OR :resolutionStatus = '' OR r.resolutionStatus = :resolutionStatus) " +
		    "AND (CAST(:startDate AS date) IS NULL OR r.createdAt >= :startDateTime) " +
		    "AND (CAST(:endDate AS date) IS NULL OR r.createdAt <= :endDateTime) ")

    Page<Reports> findReportsWithFilters(
             @Param("keyword") String keyword,
             @Param("type") String type,
             @Param("status") String status,
             @Param("resolutionStatus") String resolutionStatus,
             
             @Param("startDate") LocalDate startDate,
             @Param("endDate") LocalDate endDate,
             @Param("startDateTime") LocalDateTime startDateTime,
             @Param("endDateTime") LocalDateTime endDateTime,
             
             Pageable pageable	
     );
    
    
    // Helper method để chuyển đổi LocalDate -> LocalDateTime
    default Page<Reports> findReportsWithFilters(String keyword, String type, String status, String resolutionStatus, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        // Gọi query chính với LocalDateTime đã chuyển đổi
        return findReportsWithFilters(keyword, type, status, resolutionStatus, startDate, endDate, startDateTime, endDateTime, pageable);
    }
    
    // Lấy 5 Reports mới nhất
    List<Reports> findTop5ByOrderByCreatedAtDesc();
    
    
    //Đếm số lượng reports cho một người dùng bị báo cáo với status và resolutionStatus cụ thể.
    long countByReportedUser_UserIdAndStatusAndResolutionStatus(Integer userId, String status, String resolutionStatus);
}
