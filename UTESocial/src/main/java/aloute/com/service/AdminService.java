package aloute.com.service;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.repository.common.ReportsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class AdminService 
{
	//Đối với user
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() 
    {
    	return userRepository.findByRoleIn(Arrays.asList("user", "manager"));
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
        return postsRepository.findAll();
    }
    
    public Posts getPostById(Integer postId) 
    {
        return postsRepository.findById(postId).orElse(null);
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
        if (postOptional.isPresent()) {
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
    
    public List<Reports> getAllReports() 
    {
        return reportsRepository.findAll();
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

    
   
	 // Các phương thức thống kê
    public long countManagedUsers() 
    {
        return userRepository.countByRoleIn(Arrays.asList("user", "manager"));
    }
    
    public long getTotalPosts() 
    {
        return postsRepository.count();
    }
    
    public long getPendingReportsCount() 
    {
        return reportsRepository.countByStatus("pending");
    }
    
    
    
}
   