package aloute.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Posts;
import aloute.com.entity.Shares;
import aloute.com.entity.User;

@Repository
public interface SharesRepository extends JpaRepository<Shares, Integer> {
    Optional<Shares> findByPostAndUser(Posts post, User user);
    void deleteByPostAndUser(Posts post, User user);
    
    /**
     * Xóa tất cả shares của blocked user từ bài viết của blocker
     * Sử dụng khi A chặn B: xóa tất cả bài đăng lại của B từ bài của A
     */
    @Modifying
    @Query("""
        DELETE FROM Shares s 
        WHERE s.user.userId = :blockedUserId 
        AND s.post.user.userId = :blockerUserId
    """)
    void deleteSharesByBlockedUserFromBlockerPosts(@Param("blockerUserId") Integer blockerUserId, 
                                                     @Param("blockedUserId") Integer blockedUserId);
    
    /**
     * Đếm số lượng shares sẽ bị xóa (dùng để log/thông báo)
     */
    @Query("""
        SELECT COUNT(s) FROM Shares s 
        WHERE s.user.userId = :blockedUserId 
        AND s.post.user.userId = :blockerUserId
    """)
    long countSharesByBlockedUserFromBlockerPosts(@Param("blockerUserId") Integer blockerUserId, 
                                                    @Param("blockedUserId") Integer blockedUserId);
    
    @Query("""
    	    SELECT p
    	    FROM Posts p
    	    JOIN FETCH p.user
    	    LEFT JOIN FETCH p.attachments
    	    WHERE p.postId IN (
    	        SELECT s.post.postId FROM Shares s WHERE s.user.userId = :userId
    	    )
    	    ORDER BY (
    	        SELECT MAX(s2.shareAt)
    	        FROM Shares s2
    	        WHERE s2.post.postId = p.postId
    	        AND s2.user.userId = :userId
    	    ) DESC
    	""")
    	List<Posts> findRepostedPostsOfUser(@Param("userId") Integer userId);

}
