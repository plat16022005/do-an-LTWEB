package aloute.com.controller.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    public boolean isNotManager(HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        return user == null || (!"manager".equals(user.getRole()) && !"Admin".equals(user.getRole()));
    }

    // Post 
    @GetMapping("/posts")
    public String managePosts(Model model, HttpSession session) 
    {
        if (isNotManager(session))
        {
            return "redirect:/access-deniel";
        }
        List<Posts> posts = managerService.getAllPosts(); 
        model.addAttribute("posts", posts);
        return "manager/posts";
    }
    
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam Integer postId, RedirectAttributes redirectAttributes, HttpSession session) 
    {
    	// Kiểm tra quyền Manager (nên có trong mọi action của Manager)
        if (isNotManager(session)) 
        {
            return "redirect:/access-deniel";
        }
        try 
        {
            managerService.deletePost(postId); // Gọi service để xóa 
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bài đăng ID: " + postId); // Thông báo thành công
        } 
        catch (Exception e) 
        {
            // Log lỗi 
            System.err.println("Error deleting post ID " + postId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa bài đăng."); // Thông báo lỗi
        }
        return "redirect:/manager/posts";
    }

    // Xem bài đăng chi tiết
    @GetMapping("/posts/view")
    public String viewPost(@RequestParam Integer postId, Model model, HttpSession session) 
    {
        if (isNotManager(session))
        {
            return "redirect:/access-deniel";
        }
        Posts post = managerService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("headerTitle", "Chi tiết Bài đăng");
        model.addAttribute("headerDescription", "Thông tin chi tiết của bài đăng.");
        return "manager/post_detail";
    }
    
    //Duyệt bài
    @PostMapping("/posts/approve")
    public String approvePost(@RequestParam Integer postId) 
    {
        managerService.approvePost(postId);
        return "redirect:/manager/posts";
    }

    // Hiển thị danh sách khiếu nại và xử lý
    @GetMapping("/reports")
    public String manageReports(Model model, HttpSession session) 
    {
        if (isNotManager(session))
        {
            return "redirect:/access-deniel";
        }
        List<Reports> reports = managerService.getAllReports();
        model.addAttribute("reports", reports);
        return "manager/reports";
    }

    @PostMapping("/reports/resolve")
    public String resolveReport(@RequestParam Integer reportId) 
    {
        managerService.resolveReport(reportId);
        return "redirect:/manager/reports";
    }

    @PostMapping("/reports/reject")
    public String rejectReport(@RequestParam Integer reportId) 
    {
        managerService.rejectReport(reportId);
        return "redirect:/manager/reports";
    }


    // Hiển thị form nhập lý do từ chối
    @GetMapping("/posts/reject")
    public String showRejectPostForm(@RequestParam Integer postId, Model model, HttpSession session) 
    {
        if (isNotManager(session))
        {
            return "redirect:/access-deniel";
        }
        Posts post = managerService.getPostById(postId);
        if (post == null) 
        {
            // Xử lý trường hợp không tìm thấy bài đăng (ví dụ: redirect về trang posts với thông báo lỗi)
            return "redirect:/manager/posts?error=PostNotFound";
        }
        model.addAttribute("post", post);
        model.addAttribute("postId", postId);
        return "manager/reject_post"; 
    }

    // Xử lý việc từ chối bài đăng
    @PostMapping("/posts/reject")
    public String rejectPost(@RequestParam Integer postId, @RequestParam String reason, HttpSession session, RedirectAttributes redirectAttributes) 
    {
        if (isNotManager(session))
        {
            return "redirect:/access-deniel";
        }
        try 
        {
            managerService.rejectPost(postId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối bài đăng ID: " + postId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi từ chối bài đăng.");
        }
        return "redirect:/manager/posts";
    }

}
