package aloute.com.service;

import aloute.com.entity.Comment;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.CommentRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostsRepository postsRepo;
    private final UserRepository userRepo;

    public CommentService(CommentRepository commentRepo,
                          PostsRepository postsRepo,
                          UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.postsRepo = postsRepo;
        this.userRepo = userRepo;
    }

    // 📥 Tạo bình luận mới hoặc reply
    public Comment createComment(Integer userId, Integer postId, Integer parentId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Nội dung bình luận không được để trống");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
        Posts post = postsRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));

        Comment c = new Comment();
        c.setUser(user);
        c.setPost(post);
        c.setContent(content.trim());
        c.setCreatedAt(LocalDateTime.now());

        if (parentId != null) {
            Comment parent = commentRepo.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận cha"));
            c.setParentComment(parent);
        }

        return commentRepo.save(c);
    }

    // 🧾 Lấy bình luận gốc của bài viết
    public List<Comment> getRootComments(Integer postId) {
        Posts post = postsRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));
        return commentRepo.findByPostAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(post);
    }

    // 🧾 Lấy reply của một bình luận
    public List<Comment> getReplies(Integer parentCommentId) {
        Comment parent = commentRepo.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận cha"));
        return commentRepo.findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(parent);
    }

    // ❤️ Toggle Like (cộng/trừ likeCount)
    public int toggleLike(Integer commentId, boolean isLike) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận"));

        int current = c.getLikeCount();
        if (isLike) {
            c.setLikeCount(current + 1);
        } else {
            c.setLikeCount(Math.max(0, current - 1));
        }

        commentRepo.save(c);
        return c.getLikeCount();
    }

    // 🗑️ Xoá mềm
    public void softDelete(Integer commentId, Long currentUserId) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận"));

        if (!c.getUser().getUserId().equals(currentUserId)) {
            throw new SecurityException("Bạn không có quyền xoá bình luận này");
        }

        c.setDeleted(true);
        commentRepo.save(c);
    }
}
