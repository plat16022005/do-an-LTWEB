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

    // Post 
    @GetMapping("/posts")
    public String managePosts(Model model, HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user == null)
        {
            return "redirect:/access-deniel";
        }
        List<Posts> posts = managerService.getAllPosts(); 
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
            managerService.deletePost(postId); // Gọi service để xóa 
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
        Posts post = managerService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("headerTitle", "Chi tiết Bài đăng");
        model.addAttribute("headerDescription", "Thông tin chi tiết của bài đăng.");
        return "admin/post_detail";
    }
    
    //Duyệt bài
    @PostMapping("/posts/approve")
    public String approvePost(@RequestParam Integer postId) 
    {
        managerService.approvePost(postId);
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
        List<Reports> reports = managerService.getAllReports();
        model.addAttribute("reports", reports);
        return "admin/reports";
    }

    @PostMapping("/reports/resolve")
    public String resolveReport(@RequestParam Integer reportId) 
    {
        managerService.resolveReport(reportId);
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/reject")
    public String rejectReport(@RequestParam Integer reportId) 
    {
        managerService.rejectReport(reportId);
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
        Posts post = managerService.getPostById(postId);
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
            managerService.rejectPost(postId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối bài đăng ID: " + postId);
        } catch (Exception e) {
            // Ghi log lỗi nếu cần
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi từ chối bài đăng.");
        }
        return "redirect:/admin/posts";
    }

}
