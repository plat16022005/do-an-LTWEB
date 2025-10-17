package aloute.com.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import aloute.com.entity.Comment;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.CommentRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.service.CommentService;
import aloute.com.service.PostLikeService;
import aloute.com.service.PostRepostService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {
    @Autowired
    private PostsRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostLikeService postLikeService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostRepostService postRepostService;
    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Posts post = postRepository.findPostWithUser(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        List<Posts> singlePostList = Collections.singletonList(post);

        model.addAttribute("post", post);
        model.addAttribute("likedPostIds", postLikeService.getLikedPostIdsByUser(user, singlePostList));
        model.addAttribute("repostedPostIds", postRepostService.getRepostedPostIdsByUser(user, singlePostList));

        // 🧾 Lấy danh sách bình luận gốc (ParentCommentID = null)
        List<Comment> rootComments = commentRepository.findRootCommentsWithUser(id);
        model.addAttribute("comments", rootComments);

        return "user/post_detail";
    }



}
