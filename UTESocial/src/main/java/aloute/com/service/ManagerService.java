package aloute.com.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import aloute.com.entity.Notification;
import aloute.com.entity.PostModeration;
import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.repository.NotificationRepository;
import aloute.com.repository.UserRepository;
import aloute.com.repository.admin.PostModerationRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.repository.common.ReportsRepository;

public class ManagerService {
    @Autowired
    private AuditLogService auditLogService;
	
	
	//Đối với user
    @Autowired
    private UserRepository userRepository;

    public Page<User> getAllUsers(Pageable pageable) 
    {
    	return userRepository.findByRoleIn(Arrays.asList("user", "manager"), pageable); //truyền pageable vào repo
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

    public List<Posts> getAllPosts() 
    {
    	return postsRepository.findAllWithUserForAdmin();
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

    @Autowired
    private NotificationRepository notificationRepository; // <-- THÊM DÒNG NÀY

    @Autowired
    private PostModerationRepository postModerationRepository;
    
    // public void rejectPost(Integer postId, String reason) 
    // {
    //     Optional<Posts> postOptional = postsRepository.findById(postId);
    //     if (postOptional.isPresent()) 
    //     {
    //         Posts post = postOptional.get();
    //         post.setStatus("rejected");
            
    //         postsRepository.save(post);
    //     }
    // }

    public void rejectPost(Integer postId, String reason) 
    {
        Optional<Posts> postOptional = postsRepository.findByIdWithUser(postId);
        if (postOptional.isPresent()) 
        {
            Posts post = postOptional.get();
            User postOwner = post.getUser();

            post.setStatus("rejected");
            postsRepository.save(post);

            // Tạo bản ghi trong PostModeration
            PostModeration moderation = new PostModeration();
            moderation.setPost(post);
            moderation.setStatus("rejected");
            moderation.setReason(reason);
            moderation.setModerator(null); 
            moderation.setReviewedAt(LocalDateTime.now());
            postModerationRepository.save(moderation);

            // Gửi thông báo đến người dùng
            if (postOwner != null) 
            {
                Notification notification = new Notification();
                notification.setUser(postOwner);
                notification.setRelatedId(post.getPostId());
                notification.setType("POST_REJECTION");

                String content = "Your post (ID: " + postId + ") has been rejected. Reason: " + reason;
                notification.setContent(content);
                notification.setCreatedAt(LocalDateTime.now());

                notification.setActorAvatar(null);

                notification.setIsRead(false);
                notificationRepository.save(notification);
            }
        }
    }
    
    /*.....................................................*/
    /*.....................................................*/
    
    //Đối với Report
    @Autowired
    private ReportsRepository reportsRepository;
    
    
    public List<Reports> getAllReports() 
    {
    	return reportsRepository.findAllWithReporterAndReportedUserOrderByCreatedAtDesc();
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
    
    public void resolveReport(Integer reportId) 
    {
        Optional<Reports> reportOptional = reportsRepository.findById(reportId);
        if (reportOptional.isPresent()) 
        {
            Reports report = reportOptional.get();
            report.setResolutionStatus("resolved");
            report.setStatus("completed");
            report.setResolvedAt(LocalDateTime.now());
            reportsRepository.save(report);
        }
    }
    
    public void rejectReport(Integer reportId) 
    {
        Optional<Reports> reportOptional = reportsRepository.findById(reportId);
        if (reportOptional.isPresent()) 
        {
            Reports report = reportOptional.get();
            report.setResolutionStatus("rejected");
            report.setStatus("completed");
            report.setResolvedAt(LocalDateTime.now());
            reportsRepository.save(report);
        }
    }

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

    
}
