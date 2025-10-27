package aloute.com.controller.manager;

import java.util.List;
import java.util.Optional; // Import Optional nếu chưa có

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // <-- THÊM IMPORT NÀY
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;
import aloute.com.service.ManagerService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    
    @Autowired
    private ManagerService managerService;

    public boolean isNotManager(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        return user == null || (!"manager".equals(user.getRole()) && !"Admin".equals(user.getRole()));
    }

    // ================== POSTS ==================

    @GetMapping("/posts")
    public String managePosts(Model model, HttpSession session) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        List<Posts> posts = managerService.getAllPosts(); 
        model.addAttribute("posts", posts);
        return "manager/posts";
    }
    
    @PostMapping("/posts/{postId}/delete")
    public String deletePost(
            @PathVariable Integer postId, 
            RedirectAttributes redirectAttributes, 
            HttpSession session
    ) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        try {
            managerService.deletePost(postId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bài đăng ID: " + postId);
        } catch (Exception e) {
            System.err.println("Error deleting post ID " + postId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa bài đăng.");
        }
        return "redirect:/manager/posts";
    }

    @GetMapping("/posts/{postId}") 
    public String viewPost(
            @PathVariable Integer postId, 
            Model model, 
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        Posts post = managerService.getPostById(postId);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bài đăng ID: " + postId); // Thêm thông báo lỗi
            return "redirect:/manager/posts";
        }
        model.addAttribute("post", post);
        model.addAttribute("headerTitle", "Chi tiết Bài đăng");
        model.addAttribute("headerDescription", "Thông tin chi tiết của bài đăng.");
        return "manager/post_detail";
    }
    
    // SỬA: Dùng PathVariable
    @PostMapping("/posts/{postId}/approve") 
    public String approvePost(
            @PathVariable Integer postId, 
            HttpSession session, 
            RedirectAttributes redirectAttributes 
    ) {
         if (isNotManager(session)) { 
            return "redirect:/access-deniel"; 
        }
        try {
            managerService.approvePost(postId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt bài đăng ID: " + postId);
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi duyệt bài đăng ID: " + postId);
        }
        return "redirect:/manager/posts";
    }

    // SỬA: Dùng PathVariable
    @GetMapping("/posts/{postId}/reject") 
    public String showRejectPostForm(
            @PathVariable Integer postId, 
            Model model, 
            HttpSession session,
            RedirectAttributes redirectAttributes 
    ) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        Posts post = managerService.getPostById(postId);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bài đăng ID: " + postId); // Thêm thông báo lỗi
            return "redirect:/manager/posts";
        }
        model.addAttribute("post", post);
        return "manager/reject_post"; 
    }

    // SỬA: Dùng PathVariable
    @PostMapping("/posts/{postId}/reject") 
    public String rejectPost(
            @PathVariable Integer postId,
            @RequestParam String reason, 
            HttpSession session, 
            RedirectAttributes redirectAttributes
    ) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        try {
            managerService.rejectPost(postId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối bài đăng ID: " + postId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi từ chối bài đăng.");
        }
        return "redirect:/manager/posts";
    }

    // ================== REPORTS ==================

    @GetMapping("/reports")
    public String manageReports(Model model, HttpSession session) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        List<Reports> reports = managerService.getAllReports();
        model.addAttribute("reports", reports);
        return "manager/reports";
    }


    @PostMapping("/reports/{reportId}/resolve") 
    public String resolveReport(
            @PathVariable Integer reportId, 
            HttpSession session, 
            RedirectAttributes redirectAttributes 
    ) {
        if (isNotManager(session)) { 
            return "redirect:/access-deniel"; 
        }
        try {
            managerService.resolveReport(reportId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xử lý khiếu nại ID: " + reportId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xử lý khiếu nại ID: " + reportId);
        }
        return "redirect:/manager/reports";
    }


    @PostMapping("/reports/{reportId}/reject") 
    public String rejectReport(
            @PathVariable Integer reportId, 
            HttpSession session, 
            RedirectAttributes redirectAttributes
    ) {
        if (isNotManager(session)) { 
            return "redirect:/access-deniel"; 
        }
        try {
            managerService.rejectReport(reportId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối khiếu nại ID: " + reportId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi từ chối khiếu nại ID: " + reportId);
        }
        return "redirect:/manager/reports";
    }

    @GetMapping("/reports/view-post/{reportId}")
    public String viewReportedPost(
            @PathVariable Integer reportId, 
            Model model, 
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (isNotManager(session)) {
            return "redirect:/access-deniel"; 
        }
        Optional<Reports> reportOpt = managerService.getReportById(reportId);
        if (reportOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khiếu nại ID: " + reportId);
            return "redirect:/manager/reports";
        }

        Reports report = reportOpt.get();
        Posts post = managerService.getPostByReportId(reportId);
        
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bài đăng liên quan đến khiếu nại ID: " + reportId);
            return "redirect:/manager/reports";
        }
        model.addAttribute("post", post);
        model.addAttribute("headerTitle", "Chi tiết Bài đăng Báo cáo");
        model.addAttribute("headerDescription", "Thông tin chi tiết của bài đăng bị báo cáo.");
        return "manager/post_detail";
    }

    

}
