package aloute.com.controller.user;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aloute.com.entity.User;
import aloute.com.repository.NotificationRepository;
import aloute.com.service.NotificationService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserNotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public UserNotificationController(NotificationRepository notificationRepository, NotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/notification")
    public String showNotificationPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/access-deniel";
        }

        // 📨 Lấy danh sách thông báo của user
        var notifications = notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

        model.addAttribute("notifications", notifications);
        return "user/notification"; // tên file .html
    }
    @GetMapping("/notification/unread-count")
    @ResponseBody
    public Map<String, Long> getUnreadNotificationCount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Map.of("count", 0L); // Trả về 0 nếu chưa đăng nhập
        }
        
        // (Chúng ta sẽ tạo hàm getUnreadCount ở bước 2b)
        long count = notificationService.getUnreadCount(user.getUserId());
        
        return Map.of("count", count);
    }
}
