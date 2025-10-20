package aloute.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.repository.common.PostsRepository;
import aloute.com.repository.common.ReportsRepository;
import aloute.com.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService 
{
    
    @Autowired
    private ReportsRepository reportRepository;
    
    @Autowired
    private PostsRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Reports createReport(Integer postId, Integer reporterId, String reason) 
    {
        // Lấy thông tin post và user
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        // Kiểm tra xem user này đã báo cáo post này chưa
        List<Reports> existingReports = reportRepository.findByPostAndReporter(post, reporter);
        if (!existingReports.isEmpty()) {
            throw new RuntimeException("Bạn đã báo cáo bài viết này rồi");
        }

        // Tạo báo cáo mới
        Reports report = new Reports();
        report.setPost(post);
        report.setReporter(reporter);
        report.setReportedUser(post.getUser()); // người bị báo cáo là người đăng bài
        report.setReason(reason);
        report.setType("post");
        report.setStatus("pending");
        report.setResolutionStatus("pending");
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    public List<Reports> getPendingReports() {
        return reportRepository.findByStatus("pending");
    }

    public Reports resolveReport(Integer reportId, User admin, String resolution, String status) {
        Reports report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo"));

        report.setResolutionStatus(resolution); // resolved hoặc rejected
        report.setStatus("completed");
        report.setResolvedBy(admin);
        report.setResolvedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    /**
     * Tạo báo cáo người dùng
     */
    public Reports createUserReport(String reportedUsername, Integer reporterId, String reason) 
    {
        // Lấy thông tin người bị báo cáo
        User reportedUser = userRepository.findByNameUser(reportedUsername);
        if (reportedUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        
        // Lấy thông tin người báo cáo
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người báo cáo"));
        
        // Không cho phép tự báo cáo bản thân
        if (reportedUser.getUserId().equals(reporter.getUserId())) {
            throw new RuntimeException("Bạn không thể báo cáo chính mình");
        }
        
        // Kiểm tra xem đã báo cáo người này chưa (chỉ kiểm tra type="user")
        List<Reports> existingReports = reportRepository.findByReportedUserAndReporterAndType(reportedUser, reporter, "user");
        if (!existingReports.isEmpty()) {
            throw new RuntimeException("Bạn đã báo cáo người dùng này rồi");
        }

        // Tạo báo cáo mới
        Reports report = new Reports();
        report.setPost(null); // Không liên quan đến bài viết
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setReason(reason);
        report.setType("user");
        report.setStatus("pending");
        report.setResolutionStatus("pending");
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }
}