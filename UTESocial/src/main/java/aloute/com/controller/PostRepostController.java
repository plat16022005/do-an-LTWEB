package aloute.com.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.entity.Shares;
import aloute.com.repository.common.PostsRepository;
import aloute.com.service.PostRepostService;
import jakarta.servlet.http.HttpSession;

@RestController
public class PostRepostController {
    @Autowired
    private PostRepostService postRepostService;
    @Autowired
    private PostsRepository postsRepository;
    /**
     * 📌 Xử lý nút repost bằng form thông thường (submit)
     */
    @PostMapping("/post/toggle-repost")
    public String toggleRepostForm(
            @RequestParam Integer postId,
            HttpSession session,
            @RequestHeader(value = "referer", required = false) String referer,
            RedirectAttributes ra) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            ra.addFlashAttribute("error", "Bạn cần đăng nhập để đăng lại bài viết.");
            return "redirect:" + (referer != null ? referer : "/home");
        }

        postRepostService.toggleRepost(postId, user);
        return "redirect:" + (referer != null ? referer : "/home");
    }

    /**
     * 📌 Xử lý nút repost bằng fetch API (JSON)
     */
    @PostMapping(path = "/post/toggle-repost/json", produces = "application/json")
    @ResponseBody
    public Map<String, Object> toggleRepostJson(
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> payload,
            HttpSession session) {
    	System.out.println("📦 Payload nhận được: " + payload);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Map.of("success", false, "message", "Bạn cần đăng nhập để đăng lại bài viết.");
        }

        Integer postId = null;
        try {
            postId = (payload.get("postId") instanceof Number)
                ? ((Number) payload.get("postId")).intValue()
                : Integer.parseInt(payload.get("postId").toString());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Thiếu hoặc sai thông tin bài viết.");
        }

        boolean reposted = postRepostService.toggleRepost(postId, user);
        int repostCount = postRepostService.getRepostCount(postId);

        return Map.of(
                "success", true,
                "reposted", reposted,
                "repostCount", repostCount
        );
    }
}
