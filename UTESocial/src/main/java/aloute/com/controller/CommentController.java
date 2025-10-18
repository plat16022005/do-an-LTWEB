package aloute.com.controller;

import aloute.com.entity.Comment;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.CommentRepository;
import aloute.com.repository.common.PostsRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostsRepository postRepository;

    // 📨 Thêm bình luận gốc
    @PostMapping("/comments/add")
    public String addComment(@RequestParam Integer postId,
                             @RequestParam String content,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        Comment c = new Comment();
        c.setUser(user);
        c.setPost(post);
        c.setContent(content);
        c.setCreatedAt(LocalDateTime.now());

        commentRepository.save(c);

        return "redirect:/posts/" + postId;
    }

    // 📨 Thêm phản hồi
    @PostMapping("/comments/reply")
    @ResponseBody
    public ResponseEntity<?> addReply(@RequestParam Integer parentId,
                                      @RequestParam String content,
                                      HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        Comment reply = new Comment();
        reply.setUser(user);
        reply.setPost(parent.getPost());
        reply.setParentComment(parent);
        reply.setContent(content);
        reply.setCreatedAt(LocalDateTime.now());

        commentRepository.save(reply);

        return ResponseEntity.ok().build();
    }

    // 🧾 Lấy replies theo comment cha
    @Transactional(readOnly = true)
    @GetMapping("/comments/{parentId}/replies")
    @ResponseBody
    public List<Map<String, Object>> getReplies(@PathVariable Integer parentId) {
        List<Comment> replies = commentRepository.findRepliesWithUser(parentId);
        return replies.stream().map(this::mapCommentRecursive).toList();
    }

    private Map<String, Object> mapCommentRecursive(Comment c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", c.getCommentId());
        m.put("avatar", c.getUser().getAvatarUrl());
        m.put("username", c.getUser().getNameUser());
        m.put("time", c.getCreatedAt().toString());
        m.put("content", c.getContent());
        m.put("likes", c.getLikeCount());

        // 🪜 Gọi đệ quy nếu có replies con
        List<Comment> childReplies = commentRepository.findRepliesWithUser(c.getCommentId());
        if (!childReplies.isEmpty()) {
            List<Map<String, Object>> children = childReplies.stream()
                    .map(this::mapCommentRecursive)
                    .toList();
            m.put("replies", children);
        } else {
            m.put("replies", List.of());
        }

        return m;
    }
    @GetMapping("/comments/{id}")
    @ResponseBody
    public Map<String, Object> getComment(@PathVariable Integer id) {
        Comment c = commentRepository.findByIdWithUser(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        Map<String, Object> m = new HashMap<>();
        m.put("id", c.getCommentId());
        m.put("avatar", c.getUser().getAvatarUrl());
        m.put("username", c.getUser().getNameUser());
        m.put("time", c.getCreatedAt().toString());
        m.put("content", c.getContent());
        m.put("likes", c.getLikeCount());
        return m;
    }

}
