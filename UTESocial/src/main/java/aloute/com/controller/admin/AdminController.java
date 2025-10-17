package aloute.com.controller.admin;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.service.AdminService;
import aloute.com.entity.AuditLogs; 
import aloute.com.repository.AuditLogRepository; 
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping; // Thêm import này
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Thêm import này

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin")
public class AdminController 
{

    @Autowired
    private AdminService adminService;
    
    
    @GetMapping
    public String adminIndex(HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
        return "redirect:/admin/dashboard";
    }
    
   
    //Trang thống kê
    @GetMapping("/dashboard")
	public String showAdminDashboard(Model model, HttpSession session) 
	{
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
    	model.addAttribute("totalUsers", adminService.countManagedUsers());	  
	    model.addAttribute("pendingReports", adminService.getPendingReportsCount());
	    model.addAttribute("approvedPostsCount", adminService.getApprovedPostsCount());
        model.addAttribute("pendingPostsCount", adminService.getPendingPostsCount());
        model.addAttribute("rejectedPostsCount", adminService.getRejectedPostsCount());
	    return "admin/dashboard";
	}
    
    
    @GetMapping("/users")
    public String manageUsers(Model model, 
				    		@RequestParam(defaultValue = "0") int page, // Tham số trang hiện tại
				            @RequestParam(defaultValue = "10") int size, // Tham số kích thước trang
				    		HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"Admin".equals(user.getRole())) 
    	{
            return "redirect:/access-deniel";
        }
    	
    	// Tạo đối tượng Pageable với sắp xếp mặc định theo UserID tăng dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
    	
    	Page<User> userPage = adminService.getAllUsers(pageable);
    	
    	model.addAttribute("userPage", userPage); // Truyền cả đối tượng Page vào model
        model.addAttribute("currentPage", page);   // Truyền trang hiện tại để dùng trong pagination
        model.addAttribute("pageSize", size);      // Truyền kích thước trang
        return "admin/users";
    }

    
    //Khoá tài khoản và mở khoá
    @GetMapping("/users/lock")
    public String showLockUserForm(@RequestParam Integer userId, Model model, HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
        // Truyền userId vào form để biết cần khóa người dùng nào
        model.addAttribute("userId", userId);
        return "admin/lock_user";
    }
    
    @PostMapping("/users/lock")
    public String lockUser(@RequestParam Integer userId, @RequestParam String reason) 
    {
        adminService.lockUser(userId, reason);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/unlock")
    public String unlockUser(@RequestParam Integer userId) 
    {
        adminService.unlockUser(userId);
        return "redirect:/admin/users";
    }
    
    
    // Thiết lập role, quyền
    @PostMapping("/users/change-role")
    public String changeUserRole(@RequestParam Integer userId, @RequestParam String newRole) 
    {
        adminService.changeUserRole(userId, newRole);
        return "redirect:/admin/users";
    }
    
    
    //Hiển thị danh sách bài đăng, xoá bài đăng, xem chi tiết bài viết, kiểm duyệt bài
    @GetMapping("/posts")
    public String managePosts(Model model, HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
        List<Posts> posts = adminService.getAllPosts(); 
        model.addAttribute("posts", posts);
        return "admin/posts";
    }
    
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam Integer postId, RedirectAttributes redirectAttributes, HttpSession session) 
    {
    	// Kiểm tra quyền Admin (nên có trong mọi action của Admin)
        User user = (User) session.getAttribute("user");
        if (user == null || !"Admin".equals(user.getRole() ) ) 
        {
            return "redirect:/access-deniel";
        }
        try 
        {
            adminService.deletePost(postId); // Gọi service để xóa 
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bài đăng ID: " + postId); // Thông báo thành công
        } 
        catch (Exception e) 
        {
            // Log lỗi 
             System.err.println("Error deleting post ID " + postId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa bài đăng."); // Thông báo lỗi
        }
        return "redirect:/admin/posts";
    }
  
    // Xem bài đăng chi tiết
    @GetMapping("/posts/view")
    public String viewPost(@RequestParam Integer postId, Model model, HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
        Posts post = adminService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("headerTitle", "Chi tiết Bài đăng");
        model.addAttribute("headerDescription", "Thông tin chi tiết của bài đăng.");
        return "admin/post_detail";
    }
    
    //Duyệt bài
    @PostMapping("/posts/approve")
    public String approvePost(@RequestParam Integer postId) 
    {
        adminService.approvePost(postId);
        return "redirect:/admin/posts";
    }

    
    
    
    // Hiển thị danh sách khiếu nại và xử lý
    @GetMapping("/reports")
    public String manageReports(Model model, HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
        List<Reports> reports = adminService.getAllReports();
        model.addAttribute("reports", reports);
        return "admin/reports";
    }

    @PostMapping("/reports/resolve")
    public String resolveReport(@RequestParam Integer reportId) 
    {
        adminService.resolveReport(reportId);
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/reject")
    public String rejectReport(@RequestParam Integer reportId) 
    {
        adminService.rejectReport(reportId);
        return "redirect:/admin/reports";
    }


    // Hiển thị form nhập lý do từ chối
    @GetMapping("/posts/reject")
    public String showRejectPostForm(@RequestParam Integer postId, Model model, HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user == null || !"Admin".equals(user.getRole())) 
        {
            return "redirect:/access-deniel";
        }
        Posts post = adminService.getPostById(postId);
        if (post == null) 
        {
            // Xử lý trường hợp không tìm thấy bài đăng (ví dụ: redirect về trang posts với thông báo lỗi)
            return "redirect:/admin/posts?error=PostNotFound";
        }
        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
        return "admin/reject_post"; // Tạo file HTML này ở bước sau
    }

    // Xử lý việc từ chối bài đăng
    @PostMapping("/posts/reject")
    public String rejectPost(@RequestParam Integer postId, @RequestParam String reason, HttpSession session, RedirectAttributes redirectAttributes) 
    {
        User user = (User) session.getAttribute("user");
        if (user == null || !"Admin".equals(user.getRole())) 
        {
            return "redirect:/access-deniel";
        }
        try 
        {
            adminService.rejectPost(postId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối bài đăng ID: " + postId);
        } catch (Exception e) {
            // Ghi log lỗi nếu cần
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi từ chối bài đăng.");
        }
        return "redirect:/admin/posts";
    }
    
    
    @Autowired
    private AuditLogRepository auditLogRepository; 
    
    @GetMapping("/audit-logs")
    public String showAuditLogs(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "15") int size,
                                HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user == null || !"Admin".equals(user.getRole())) 
        {
            return "redirect:/access-deniel";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLogs> logPage = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable); 

        model.addAttribute("logPage", logPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "admin/audit_logs"; 
    }
}