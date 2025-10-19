package aloute.com.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import aloute.com.entity.User;
import aloute.com.repository.NotificationRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserNotificationController {

    private final NotificationRepository notificationRepository;

    public UserNotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
}
