package aloute.com.controller.user;

import aloute.com.entity.User;
import aloute.com.service.BlockService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller xử lý chặn/bỏ chặn người dùng
 */
@RestController
@RequestMapping("/user")
public class UserBlockController {

    @Autowired
    private BlockService blockService;

    /**
     * API chặn người dùng
     * POST /user/block
     * Body: { "blockedUserId": 123 }
     */
    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockUser(
            @RequestBody Map<String, Integer> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lấy user từ session
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Lấy ID người bị chặn từ request
            Integer blockedUserId = request.get("blockedUserId");
            if (blockedUserId == null) {
                response.put("success", false);
                response.put("message", "Thiếu thông tin người dùng cần chặn");
                return ResponseEntity.badRequest().body(response);
            }

            // Gọi service chặn người dùng
            String result = blockService.blockUser(currentUser.getUserId(), blockedUserId);
            
            // Kiểm tra kết quả
            if (result.contains("thành công")) {
                response.put("success", true);
                response.put("message", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", result);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API bỏ chặn người dùng
     * POST /user/unblock
     * Body: { "blockedUserId": 123 }
     */
    @PostMapping("/unblock")
    public ResponseEntity<Map<String, Object>> unblockUser(
            @RequestBody Map<String, Integer> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lấy user từ session
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Lấy ID người bị chặn từ request
            Integer blockedUserId = request.get("blockedUserId");
            if (blockedUserId == null) {
                response.put("success", false);
                response.put("message", "Thiếu thông tin người dùng cần bỏ chặn");
                return ResponseEntity.badRequest().body(response);
            }

            // Gọi service bỏ chặn người dùng
            String result = blockService.unblockUser(currentUser.getUserId(), blockedUserId);
            
            // Kiểm tra kết quả
            if (result.contains("thành công")) {
                response.put("success", true);
                response.put("message", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", result);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
