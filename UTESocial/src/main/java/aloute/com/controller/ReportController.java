package aloute.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import aloute.com.entity.User;
import aloute.com.service.ReportService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/report-post")
    public ResponseEntity<?> reportPost(
            @RequestParam Integer postId,
            @RequestParam String reason,
            HttpSession session) {
        try {
            User reporter = (User) session.getAttribute("user");
            if (reporter == null) {
                return ResponseEntity.status(401)
                        .body("Bạn cần đăng nhập để báo cáo");
            }

            reportService.createReport(postId, reporter.getUserId(), reason);

            return ResponseEntity.ok()
                    .body("Báo cáo đã được gửi thành công. Chúng tôi sẽ xem xét báo cáo của bạn.");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Có lỗi xảy ra khi gửi báo cáo");
        }
    }
}