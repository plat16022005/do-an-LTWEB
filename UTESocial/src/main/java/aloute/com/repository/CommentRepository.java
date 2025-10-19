package aloute.com.repository;

import aloute.com.entity.Comment;
import aloute.com.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // Đếm tổng số bình luận (bao gồm cả reply) cho một bài viết (không tính comment bị xóa)
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.postId = :postId AND c.isDeleted = false")
    int countByPostId(@Param("postId") Integer postId);

    // 🧾 Lấy tất cả bình luận gốc của một bài viết
    List<Comment> findByPostAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Posts post);

    // 🧾 Lấy tất cả reply của một bình luận
    List<Comment> findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(Comment parent);
    
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.postId = :postId AND c.parentComment IS NULL AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Comment> findRootCommentsWithUser(@Param("postId") Integer postId);
    // 🔸 Lấy bình luận cha của 1 bài viết
    List<Comment> findByPost_PostIdAndParentCommentIsNullOrderByCreatedAtDesc(Integer postId);

    // 🔸 Lấy replies theo ID của bình luận cha
    List<Comment> findByParentComment_CommentIdOrderByCreatedAtAsc(Integer parentId);
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.parentComment.commentId = :parentId ORDER BY c.createdAt ASC")
    List<Comment> findRepliesWithUser(@Param("parentId") Integer parentId);
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.commentId = :id")
    Optional<Comment> findByIdWithUser(@Param("id") Integer id);

}
