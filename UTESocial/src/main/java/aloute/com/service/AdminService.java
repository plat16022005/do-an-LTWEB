package aloute.com.service;

import aloute.com.entity.Notification;
import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.repository.NotificationRepository;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.repository.common.ReportsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional; 

import org.hibernate.Hibernate; 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class AdminService 
{
	@Autowired
    private AuditLogService auditLogService;
	
	
	//Đối với user
    @Autowired
    private UserRepository userRepository;

    public Page<User> findUsersWithFilters(String keyword, String role, String status, Pageable pageable) 
    {
        Boolean isLocked = null;
        if ("active".equalsIgnoreCase(status)) {
            isLocked = false;
        } else if ("locked".equalsIgnoreCase(status)) {
            isLocked = true;
        }

        return userRepository.findUsersWithFilters(keyword, role, isLocked, pageable);
    }
    
    
    //Khoá tài khoản
    public void lockUser(Integer userId, String reason) 
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) 
        {
            User user = userOptional.get();
            user.setIsLocked(true);
            user.setLockedReason(reason);
            user.setLockedAt(LocalDateTime.now());
            userRepository.save(user);
            
            auditLogService.logAction("LOCK_USER", "Locked user ID: " + userId + ". Reason: " + reason);
        }
    }
    
    //Mở khoá tài khoản
    public void unlockUser(Integer userId) 
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) 
        {
            User user = userOptional.get();
            user.setIsLocked(false);
            user.setLockedReason(null);
            user.setLockedAt(null);
            userRepository.save(user);
            
            auditLogService.logAction("UNLOCK_USER", "Unlocked user ID: " + userId);
        }
    }
    
    //Thiết lập quyền, role
    public void changeUserRole(Integer userId, String newRole) 
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) 
        {
            User user = userOptional.get();
            // Đảm bảo chỉ có thể thay đổi role hợp lệ
            if ("user".equals(newRole) || "manager".equals(newRole) || "admin".equals(newRole)) 
            {
                user.setRole(newRole);
                userRepository.save(user);
            }
        }
    }
    
    /*.....................................................*/
    /*.....................................................*/
    
    //Đối với bài viết
    
    @Autowired
    private PostsRepository postsRepository;

    public Page<Posts> findPostsWithFilters(String keyword, String status, LocalDate startDate, LocalDate endDate, Pageable pageable)
    {
    	return postsRepository.findPostsWithFiltersForAdmin(keyword, status, startDate, endDate, pageable);
    }
    
    @Transactional(readOnly = true)
    public Posts getPostById(Integer postId) 
    {
    	Optional<Posts> postOptional = postsRepository.findByIdWithUser(postId);
        if (postOptional.isPresent()) 
        {
            Posts post = postOptional.get();

            // Khởi tạo các collection 
            // lấy dữ liệu attachments và moderations ( lịch sử duyệt/từ chối)
            Hibernate.initialize(post.getAttachments());
            Hibernate.initialize(post.getModerations());

            
            if (post.getModerations() != null) 
            {
                post.getModerations().forEach(mod -> Hibernate.initialize(mod.getModerator()));
            }

            return post; // Trả về post với các collection đã được tải
        }
        return null; 
    }

    public void deletePost(Integer postId) 
    {
        Optional<Posts> postOptional = postsRepository.findById(postId);
        if (postOptional.isPresent()) 
        {
            Posts post = postOptional.get();
            post.setDeleted(true); // Đánh dấu là đã xóa 
            postsRepository.save(post);
        }
    }
    
    public void approvePost(Integer postId) 
    {
        Optional<Posts> postOptional = postsRepository.findById(postId);
        if (postOptional.isPresent()) {
            Posts post = postOptional.get();
            post.setStatus("approved");
            postsRepository.save(post);
        }
    }
    
    public void rejectPost(Integer postId, String reason) 
    {
        Optional<Posts> postOptional = postsRepository.findById(postId);
        if (postOptional.isPresent()) 
        {
            Posts post = postOptional.get();
            post.setStatus("rejected");
            
            postsRepository.save(post);
        }
    }
    
    /*.....................................................*/
    /*.....................................................*/
    
    //Đối với Report
    @Autowired
    private ReportsRepository reportsRepository;
    
    
    public Page<Reports> findReportsWithFilters(String keyword, String type, String status, String resolutionStatus, LocalDate startDate, LocalDate endDate, Pageable pageable) 
    {
    	return reportsRepository.findReportsWithFilters(keyword, type, status, resolutionStatus, startDate, endDate, pageable);
    }
    
    public long countResolvedReportsAgainstUser(Integer userId) 
    {
        if (userId == null) 
        {
            return 0; 
        }
        // Lấy với status="completed" và resolutionStatus="resolved"
        return reportsRepository.countByReportedUser_UserIdAndStatusAndResolutionStatus(userId, "completed", "resolved");
    }
    
    @Transactional(readOnly = true) 
    public Optional<Reports> getReportWithDetails(Integer reportId) 
    {
    	Optional<Reports> reportOpt = reportsRepository.findByIdWithDetails(reportId);

        if (reportOpt.isPresent()) 
        {
            Reports report = reportOpt.get();
            Posts post = report.getPost(); // Lấy post từ report

            // Kiểm tra xem report này có liên quan đến post không
            if (post != null) 
            {
                // Khởi tạo collection moderations của post đó
                Hibernate.initialize(post.getModerations());

                // Nếu moderations không null, khởi tạo luôn moderator bên trong
                if (post.getModerations() != null) 
                {
                    post.getModerations().forEach(mod -> Hibernate.initialize( mod.getModerator()) );
                }
            }
        }

        return reportOpt;
    }
    
    @Transactional
    public void resolveReport(Integer reportId) 
    {
    	Optional<Reports> reportOptional = reportsRepository.findByIdWithDetails(reportId);
        if (reportOptional.isPresent()) 
        {
            Reports report = reportOptional.get();
            report.setResolutionStatus("resolved");
            report.setStatus("completed");
            report.setResolvedAt(LocalDateTime.now());
            reportsRepository.save(report);
            
            // Gửi thông báo 
            User reporter = report.getReporter();
            User reportedUser = report.getReportedUser(); 
            Posts reportedPost = report.getPost();       

            // 1. Thông báo cho người tố cáo (reporter)
            sendNotification(
                reporter,
                "SYSTEM",
                report.getReportId(),
                "Khiếu nại (ID: " + report.getReportId() + ") của bạn đã được xác nhận và xử lý."
            );

            // 2. Thông báo cho người bị tố cáo
            if ("user".equals(report.getType()) && reportedUser != null) 
            {
                // Nếu là khiếu nại người dùng
                 sendNotification(
                    reportedUser,
                    "SYSTEM", 
                    report.getReportId(),
                    "Bạn đã nhận được một khiếu nại (ID: " + report.getReportId() + ") đã được xác nhận. Vui lòng xem xét lại hành vi của mình."
                 );
            } 
            else if ("post".equals(report.getType()) && reportedPost != null && reportedPost.getUser() != null) 
            {
                 // Nếu là khiếu nại bài viết, thông báo cho người đăng bài
                 sendNotification(
                    reportedPost.getUser(),
                    "SYSTEM", 
                    report.getReportId(),
                    "Bài viết (ID: " + reportedPost.getPostId() + ") của bạn đã bị khiếu nại và khiếu nại đã được xác nhận. Vui lòng xem xét lại nội dung."
                 );
            }
        }
        else 
        {
            System.err.println("Không tìm thấy Report ID: " + reportId + " để xử lý.");
        }
    }
    
    
    @Transactional
    public void rejectReport(Integer reportId) 
    {
    	Optional<Reports> reportOptional = reportsRepository.findByIdWithDetails(reportId);
        if (reportOptional.isPresent()) 
        {
            Reports report = reportOptional.get();
            report.setResolutionStatus("rejected");
            report.setStatus("completed");
            report.setResolvedAt(LocalDateTime.now());
            reportsRepository.save(report);
            
            // Gửi thông báo
            User reporter = report.getReporter();

            // Thông báo cho người tố cáo (reporter)
            sendNotification(
                reporter,
                "SYSTEM",
                report.getReportId(),
                "Khiếu nại (ID: " + report.getReportId() + ") của bạn đã bị từ chối do không đủ cơ sở." // Lý do cố định
            );
        }
        else 
        {
            System.err.println("Không tìm thấy Report ID: " + reportId + " để từ chối.");
        }
    }
    
    

    
   
	 // Các phương thức thống kê
    public long countManagedUsers() 
    {
        return userRepository.countByRoleIn(Arrays.asList("user", "manager"));
    }
    
    
    public long getPendingReportsCount() 
    {
        return reportsRepository.countByStatus("pending");
    }
    
    
    public long getApprovedPostsCount() 
    {
        return postsRepository.countByStatusAndIsDeletedFalse("approved");
    }

    public long getPendingPostsCount() 
    {
        return postsRepository.countByStatusAndIsDeletedFalse("pending");
    }

    public long getRejectedPostsCount() {
        return postsRepository.countByStatusAndIsDeletedFalse("rejected");
    }
    
   
    //Đếm số người dùng đang hoạt động, đếm User và Manager.
    public long countActiveUsers() 
    {
        return userRepository.countByRoleInAndIsLockedFalse(Arrays.asList("user", "manager"));
    }

    
    // Đếm số người dùng đã bị khóa, đếm User và Manager
    public long countLockedUsers() 
    {
        return userRepository.countByRoleInAndIsLockedTrue(Arrays.asList("user", "manager"));
    }
    
    //Đếm số người dùng theo vai trò
    public long countUsersByRole(String role) 
    {
        return userRepository.countByRole(role);
    }
    
    
    
    //Thông báo
    @Autowired
    private NotificationRepository notificationRepository;
    
    
    @Transactional // Đảm bảo tất cả thông báo được lưu hoặc không lưu gì cả
    public void createGlobalAnnouncement(String content, User adminUser) 
    {
        if (content == null || content.isBlank()) 
        {
            throw new IllegalArgumentException("Nội dung thông báo không được để trống.");
        }

        // 1. Lấy danh sách tất cả người dùng có vai trò 'user' hoặc 'manager'
        List<User> targetUsers = userRepository.findByRoleIn(Arrays.asList("user", "manager"), Pageable.unpaged()).getContent();

        if (targetUsers.isEmpty()) 
        {
            System.out.println("Không có người dùng nào (user/manager) để gửi thông báo.");
            return; // Không có ai để gửi thì dừng lại
        }

        List<Notification> notificationsToSave = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 2. Tạo đối tượng Notification cho từng người dùng
        for (User targetUser : targetUsers) {
            Notification notification = new Notification();
            notification.setUser(targetUser); // Người nhận thông báo
            notification.setType("SYSTEM"); 
            notification.setRelatedId(null); // Không liên quan đến ID cụ thể nào
            notification.setContent(content); // Nội dung từ admin
            notification.setCreatedAt(now);
            notification.setIsRead(false);
            
            
            notificationsToSave.add(notification);
        }

        // 3. Lưu tất cả thông báo vào database (hiệu quả hơn lưu từng cái)
        notificationRepository.saveAll(notificationsToSave);

        // 4. Ghi lại hành động vào Audit Log
        auditLogService.logAction(adminUser, "CREATE_ANNOUNCEMENT", "Sent announcement to " + targetUsers.size() + " users. Content: " + content.substring(0, Math.min(content.length(), 100)) + "..."); // Ghi log, giới hạn độ dài nội dung

        System.out.println("Đã tạo " + notificationsToSave.size() + " thông báo hệ thống.");
    }
    
    
    
    private void sendNotification(User recipient, String type, Integer relatedId, String content) 
    {
        if (recipient == null) return; // Không gửi nếu không có người nhận

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(type); // Dùng type SYSTEM chung
        notification.setRelatedId(relatedId); // Có thể dùng ID của report
        notification.setContent(content);
        
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
        System.out.println("Đã gửi thông báo '" + type + "' đến User ID: " + recipient.getUserId());
    }
}
   