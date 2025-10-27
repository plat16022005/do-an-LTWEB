package aloute.com.controller.admin;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.entity.AuditLogs; 
import aloute.com.entity.Reports;

import aloute.com.repository.UserRepository;
import aloute.com.repository.AuditLogRepository; 
import aloute.com.repository.common.ReportsRepository;

import aloute.com.service.AdminService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    @Autowired 
    private ReportsRepository reportsRepository;
    @Autowired 
    private UserRepository userRepository;
    
    
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
    	if (user == null || !"Admin".equals(user.getRole()) )
    	{
    		return "redirect:/access-deniel";
    	}
    	//Thống kê cơ bản 
    	model.addAttribute("totalUsers", adminService.countManagedUsers());	  
	    model.addAttribute("pendingReports", adminService.getPendingReportsCount());
	    model.addAttribute("approvedPostsCount", adminService.getApprovedPostsCount());
        model.addAttribute("pendingPostsCount", adminService.getPendingPostsCount());
        model.addAttribute("rejectedPostsCount", adminService.getRejectedPostsCount());
        
        // 1. Lấy 5 Audit Logs mới nhất (Fetch cả User nếu có)
        Pageable latestFiveLogs = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        List<AuditLogs> recentActivityLogs = auditLogRepository.findAllWithUserOrderByCreatedAtDesc(latestFiveLogs).getContent();
        model.addAttribute("recentActivityLogs", recentActivityLogs);
        
        // 2. Lấy 5 khiếu nại mới nhất
        List<Reports> recentReports = reportsRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("recentReports", recentReports);
        
        //3. Thống kê user chi tiết 
        model.addAttribute("activeUsers", adminService.countActiveUsers());
        model.addAttribute("lockedUsers", adminService.countLockedUsers());
        
        //4. Lấy 5 user mới đăng ký
        Pageable latestFiveUsers = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        List<User> recentUsers = userRepository.findByRoleInOrderByCreatedAtDesc
        (
            Arrays.asList("user", "manager"), // Chỉ lấy role user và manager
            latestFiveUsers
        );
        model.addAttribute("recentUsers", recentUsers);
        
	    return "admin/dashboard";
	}
    
    
    @GetMapping("/users")
    public String manageUsers(Model model, 
				    		@RequestParam(defaultValue = "0") int page, // Tham số trang hiện tại
				            @RequestParam(defaultValue = "10") int size, // Tham số kích thước trang
				            @RequestParam(required = false) String keyword, // Thêm keyword
	                        @RequestParam(required = false) String role,    // Thêm role
	                        @RequestParam(required = false) String status,  //Thêm trạng thái
				    		HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"Admin".equals(user.getRole())) 
    	{
            return "redirect:/access-deniel";
        }
    	
    	// Tạo đối tượng Pageable với sắp xếp mặc định theo UserID tăng dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
    	
        Page<User> userPage = adminService.findUsersWithFilters(keyword, role, status, pageable);
    	
    	model.addAttribute("userPage", userPage); // Truyền cả đối tượng Page vào model
        model.addAttribute("currentPage", page);   // Truyền trang hiện tại để dùng trong pagination
        model.addAttribute("pageSize", size);      // Truyền kích thước trang
        model.addAttribute("pageTitle", "Người dùng"); //Thêm một thuộc tính (attribute) vào Model để cho biết tên của trang
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("status", status);
        
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
    public String managePosts(	Model model, 
					    		@RequestParam(defaultValue = "0") int page, 	// tham số phân trang
					            @RequestParam(defaultValue = "10") int size,	// tham số phân trang
					    		@RequestParam(required = false) String keyword,
					            @RequestParam(required = false) String status,
					            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,					            
    							HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"Admin".equals(user.getRole()) )
    	{
    		return "redirect:/access-deniel";
    	}
    	
    	Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());	// Sắp xếp theo ngày tạo mới nhất
    	Page<Posts> postPage = adminService.findPostsWithFilters(keyword, status, startDate, endDate, pageable);	//Truyền pageable, nhận Page<Posts>
    	
    	model.addAttribute("postPage", postPage);
        
        model.addAttribute("currentPage", page); 
        model.addAttribute("pageSize", size);
        
        model.addAttribute("pageTitle", "Bài đăng");  //Thêm một thuộc tính (attribute) vào Model để cho biết tên của trang
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        
        model.addAttribute("startDate", (startDate != null) ? startDate.toString() : "");
        model.addAttribute("endDate", (endDate != null) ? endDate.toString() : "");
        
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
    public String manageReports(Model model,
					    		@RequestParam(defaultValue = "0") int page,
					            @RequestParam(defaultValue = "10") int size,
					            @RequestParam(required = false) String keyword,
					            @RequestParam(required = false) String type,
					            @RequestParam(required = false) String status,
					            @RequestParam(required = false) String resolutionStatus,
					            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    							HttpSession session) 
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"Admin".equals(user.getRole()) )
    	{
    		return "redirect:/access-deniel";
    	}
    	
    	Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    	Page<Reports> reportPage = adminService.findReportsWithFilters(keyword, type, status, resolutionStatus,startDate, endDate, pageable);
    	
    	model.addAttribute("reportPage", reportPage);
    	model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("resolutionStatus", resolutionStatus);
        
        model.addAttribute("startDate", (startDate != null) ? startDate.toString() : "");
        model.addAttribute("endDate", (endDate != null) ? endDate.toString() : "");
        
        model.addAttribute("pageTitle", "Khiếu nại"); //Thêm một thuộc tính (attribute) vào Model để cho biết tên của trang
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
    
    
    // Xem log hoạt động
    @Autowired
    private AuditLogRepository auditLogRepository; 
    
    @GetMapping("/audit-logs")
    public String showAuditLogs(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "15") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String action,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user == null || !"Admin".equals(user.getRole())) 
        {
            return "redirect:/access-deniel";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLogs> logPage = auditLogRepository.findLogsWithFilters(keyword, action, startDate, endDate, pageable);

        model.addAttribute("logPage", logPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("pageTitle", "Lịch sử"); //Thêm một thuộc tính (attribute) vào Model để cho biết tên của trang
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("action", action);
        model.addAttribute("startDate", (startDate != null) ? startDate.toString() : "");
        model.addAttribute("endDate", (endDate != null) ? endDate.toString() : "");
        
        return "admin/audit_logs"; 
    }
    
    
    //Xem chi tiết report bài viết
    @GetMapping("/reports/view-post/{reportId}")
    public String viewReportedPost(@PathVariable Integer reportId, Model model, HttpSession session) 
    {
        User adminUser = (User) session.getAttribute("user");
        if ( adminUser == null || !"Admin".equals(adminUser.getRole()) ) 
        {
            return "redirect:/access-deniel";
        }

        Optional<Reports> reportOpt = adminService.getReportWithDetails(reportId);

        if (reportOpt.isEmpty() || !"post".equals(reportOpt.get().getType()) || reportOpt.get().getPost() == null) 
        {
            
            model.addAttribute("errorMessage", "Không tìm thấy khiếu nại hoặc bài viết liên quan.");
            return "admin/report_not_found";
            
        }

        Reports report = reportOpt.get();
        model.addAttribute("report", report);
        model.addAttribute("post", report.getPost());
        model.addAttribute("headerTitle", "Chi tiết Khiếu nại Bài viết");
        model.addAttribute("headerDescription", "Xem thông tin khiếu nại và bài viết bị khiếu nại.");

        return "admin/report_post_detail"; 
    }
    
    
  //Xem chi tiết report người dùng
    @GetMapping("/reports/view-user/{reportId}")
    public String viewReportedUser(@PathVariable Integer reportId, Model model, HttpSession session) 
    {
        User adminUser = (User) session.getAttribute("user");
        if (adminUser == null || !"Admin".equals(adminUser.getRole())) {
            return "redirect:/access-deniel";
        }

        Optional<Reports> reportOpt = adminService.getReportWithDetails(reportId);

        if (reportOpt.isEmpty() || !"user".equals(reportOpt.get().getType()) || reportOpt.get().getReportedUser() == null) {
            model.addAttribute("errorMessage", "Không tìm thấy khiếu nại hoặc người dùng liên quan.");
            return "admin/report_not_found";
        }

        Reports report = reportOpt.get();
        User reportedUser = report.getReportedUser();
        
        long resolvedReportCount = adminService.countResolvedReportsAgainstUser(reportedUser.getUserId());
        
        model.addAttribute("report", report);
        model.addAttribute("reportedUser", report.getReportedUser());
        model.addAttribute("resolvedReportCount", resolvedReportCount);
        model.addAttribute("headerTitle", "Chi tiết Khiếu nại Người dùng");
        model.addAttribute("headerDescription", "Xem thông tin khiếu nại và người dùng bị khiếu nại.");

        return "admin/report_user_detail";
    }
    
    
    //Xem chi tiết thông tin người dùng
    @GetMapping("/users/view/{userId}")
    public String viewUserDetails(@PathVariable Integer userId, Model model, HttpSession session) 
    {
        User adminUser = (User) session.getAttribute("user");
        if (adminUser == null || !"Admin".equals(adminUser.getRole())) 
        {
            return "redirect:/access-deniel";
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || "Admin".equals(userOpt.get().getRole())) 
        {
             model.addAttribute("errorMessage", "Không tìm thấy người dùng hoặc không thể xem chi tiết.");
             return "admin/report_not_found"; 
        }

        User targetUser = userOpt.get();
        long resolvedReportCount = adminService.countResolvedReportsAgainstUser(userId);

        model.addAttribute("targetUser", targetUser);
        model.addAttribute("resolvedReportCount", resolvedReportCount);
        model.addAttribute("pageTitle", "Chi tiết Người dùng"); 
        model.addAttribute("keyword", "");
        model.addAttribute("role", "");
        model.addAttribute("status", "");


        return "admin/user_detail"; 
    }
}