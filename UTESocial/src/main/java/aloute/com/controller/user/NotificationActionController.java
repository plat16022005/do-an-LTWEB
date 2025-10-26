package aloute.com.controller.user;

import aloute.com.entity.Notification;
import aloute.com.repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NotificationActionController {

    private final NotificationRepository notificationRepository;

    public NotificationActionController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/notification/read/{id}")
    public String readNotification(@PathVariable("id") Integer id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        // ✅ Đánh dấu đã đọc
        n.setIsRead(true);
        notificationRepository.save(n);

        // ✅ Xác định đường dẫn chuyển hướng
        String redirectUrl;
        switch (n.getType()) {
            case "LIKE":
            case "COMMENT":
            case "SHARE":
            case "FRIEND_POST":
                redirectUrl = "/posts/" + n.getRelatedId();
                break;
            case "FRIEND_REQUEST":
            case "FRIEND_ACCEPT":
                redirectUrl = "/profile/" + n.getRelatedId();
                break;
            case "MESSAGE":
                redirectUrl = "/message/" + n.getRelatedId();
                break;
            case "GROUP_MESSAGE":
                redirectUrl = "/message/group/" + n.getRelatedId();
                break;                
            default:
                redirectUrl = "/notification";
                break;
        }

        // 🔁 Redirect người dùng
        return "redirect:" + redirectUrl;
    }
}
