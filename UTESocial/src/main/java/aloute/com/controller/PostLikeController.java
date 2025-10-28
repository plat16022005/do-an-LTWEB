package aloute.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.User;
import aloute.com.service.PostLikeService;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    @PostMapping("/post/toggle-like")
    public String toggleLikeForm(@RequestParam Integer postId, HttpSession session, @RequestHeader(value = "referer", required = false) String referer, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("error", "Bạn cần đăng nhập");
            return "redirect:" + (referer != null ? referer : "/home");
        }

        postLikeService.toggleLike(postId, user);
        return "redirect:" + (referer != null ? referer : "/home");
    }

    // JSON/AJAX version
    @PostMapping(path = "/post/toggle-like/json", produces = "application/json")
    @ResponseBody
    public Map<String, Object> toggleLikeJson(@org.springframework.web.bind.annotation.RequestBody Map<String, Object> payload, HttpSession session) {
    	System.out.println("📦 Payload nhận được: " + payload);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Map.of("success", false, "message", "Bạn cần đăng nhập để thích bài viết.");
        }
        Integer postId = null;
        try {
            postId = (payload.get("postId") instanceof Number)
                ? ((Number) payload.get("postId")).intValue()
                : Integer.parseInt(payload.get("postId").toString());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Thiếu hoặc sai thông tin bài viết.");
        }
        boolean liked = postLikeService.toggleLike(postId, user);
        int likesCount = postLikeService.getLikesCount(postId);
        return Map.of("success", true, "liked", liked, "likesCount", likesCount);
    // Add this method to PostLikeService:
    // public int getLikesCount(Integer postId) {
    //     Optional<Posts> post = postsRepository.findById(postId);
    //     return post.map(Posts::getLikesCount).orElse(0);
    // }
    }
    @GetMapping("/post/{postId}/likers")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPostLikers(@PathVariable Integer postId) {
        try {
            List<User> likers = postLikeService.getLikersForPost(postId);

            // Chuyển List<User> thành List<Map> để chỉ lấy thông tin cần thiết
            List<Map<String, Object>> result = likers.stream()
                .map(user -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", user.getUserId());
                    userInfo.put("fullName", user.getFullName());
                    userInfo.put("avatarUrl", user.getAvatarUrl());
                    userInfo.put("nameUser", user.getNameUser()); // Thêm username để tạo link profile
                    return userInfo;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(result); // Trả về 200 OK và danh sách
        } catch (Exception e) {
            // Log lỗi ra server
            System.err.println("Lỗi khi lấy danh sách người thích bài viết " + postId + ": " + e.getMessage());
            // Trả về lỗi 500
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
