package aloute.com.repository.common;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

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
    
    
    //Lấy danh sách tố cáo, khiếu nại
    @Query("SELECT DISTINCT r FROM Reports r " +
    	       "LEFT JOIN FETCH r.reporter " +
    	       "LEFT JOIN FETCH r.reportedUser " +
    	       "LEFT JOIN FETCH r.post p " + // Fetch post 
    	       "ORDER BY r.createdAt DESC") // Sắp xếp theo ngày tạo mới nhất lên đầu
    	List<Reports> findAllWithReporterAndReportedUserOrderByCreatedAtDesc();
}
