package aloute.com.repository;

import aloute.com.entity.Comment;
import aloute.com.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 🧾 Lấy tất cả bình luận gốc của một bài viết
    List<Comment> findByPostAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Posts post);

    // 🧾 Lấy tất cả reply của một bình luận
    List<Comment> findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(Comment parent);
    
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.postId = :postId AND c.parentComment IS NULL AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findRootCommentsWithUser(@Param("postId") Integer postId);

}
