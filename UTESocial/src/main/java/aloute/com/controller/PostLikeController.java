package aloute.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.User;
import aloute.com.service.PostLikeService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

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
}
