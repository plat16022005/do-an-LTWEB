package aloute.com.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aloute.com.entity.User;
import aloute.com.service.BlockService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserBlockController {

    @Autowired
    private BlockService blockService;

    /**
     * Chặn người dùng
     */
    @PostMapping("/block")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> blockUser(@RequestBody Map<String, Integer> request, 
                                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Integer blockedUserId = request.get("blockedUserId");
        if (blockedUserId == null) {
            response.put("success", false);
            response.put("message", "Thiếu thông tin người dùng cần chặn");
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = blockService.blockUser(currentUser.getUserId(), blockedUserId);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Đã chặn người dùng thành công");
        } else {
            response.put("success", false);
            response.put("message", "Không thể chặn người dùng này. Có thể bạn đã là bạn bè hoặc đã có mối quan hệ chặn.");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Bỏ chặn người dùng
     */
    @PostMapping("/unblock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unblockUser(@RequestBody Map<String, Integer> request, 
                                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Integer blockedUserId = request.get("blockedUserId");
        if (blockedUserId == null) {
            response.put("success", false);
            response.put("message", "Thiếu thông tin người dùng");
            return ResponseEntity.badRequest().body(response);
        }

        boolean success = blockService.unblockUser(currentUser.getUserId(), blockedUserId);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Đã bỏ chặn người dùng thành công");
        } else {
            response.put("success", false);
            response.put("message", "Không thể bỏ chặn người dùng này");
        }
        
        return ResponseEntity.ok(response);
    }
}
