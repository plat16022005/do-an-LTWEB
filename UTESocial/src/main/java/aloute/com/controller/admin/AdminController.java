package aloute.com.controller.admin;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController 
{

    @Autowired
    private AdminService adminService;
    
    
    @GetMapping
    public String adminIndex() 
    {
        return "redirect:/admin/dashboard";
    }
    
   
    //Trang thống kê
    @GetMapping("/dashboard")
	public String showAdminDashboard(Model model) 
	{
    	model.addAttribute("totalUsers", adminService.countManagedUsers());
	    model.addAttribute("totalPosts", adminService.getTotalPosts());
	    model.addAttribute("pendingReports", adminService.getPendingReportsCount());
	    return "admin/dashboard";
	}
    
    
    @GetMapping("/users")
    public String manageUsers(Model model) 
    {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    
    //Khoá tài khoản và mở khoá
    @GetMapping("/users/lock")
    public String showLockUserForm(@RequestParam Integer userId, Model model) 
    {
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
    public String managePosts(Model model) 
    {
        List<Posts> posts = adminService.getAllPosts(); 
        model.addAttribute("posts", posts);
        return "admin/posts";
    }

    public String deletePost(@RequestParam Integer postId) 
    {
        adminService.deletePost(postId);
        return "redirect:/admin/posts";
    }
  
    // Xem bài đăng chi tiết
    @GetMapping("/posts/view")
    public String viewPost(@RequestParam Integer postId, Model model) 
    {
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
    public String manageReports(Model model) 
    {
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


     
}