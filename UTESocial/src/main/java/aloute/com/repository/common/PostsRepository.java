package aloute.com.repository.common;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
	@Query("""
		    SELECT DISTINCT p FROM Posts p
		    LEFT JOIN FETCH p.user
		    LEFT JOIN FETCH p.attachments
		    ORDER BY FUNCTION('RAND')
		""")
        List<Posts> findAllWithUserAndAttachments();
    @Query("""
            SELECT DISTINCT p FROM Posts p
            LEFT JOIN FETCH p.user
            LEFT JOIN FETCH p.attachments
            WHERE p.user.userId IN (
                SELECT CASE
                           WHEN f.user1.userId = :currentUserId THEN f.user2.userId
                           ELSE f.user1.userId
                       END
                FROM Friend f
                WHERE (f.user1.userId = :currentUserId OR f.user2.userId = :currentUserId)
                  AND f.status = 'Accepted'
            )
            ORDER BY p.createdAt DESC
        """)
        List<Posts> findFriendPosts(@Param("currentUserId") Integer currentUserId);

	@Query("""
		    SELECT DISTINCT p FROM Posts p
		    LEFT JOIN FETCH p.user u
		    LEFT JOIN FETCH p.attachments
		    WHERE u.userId = :userId
		    ORDER BY p.createdAt DESC
		""")
		List<Posts> findPostsOfUser(@Param("userId") Integer userId);

	@Query("""
		    SELECT DISTINCT p FROM Posts p
		    LEFT JOIN FETCH p.user u
		    LEFT JOIN FETCH p.attachments
		    WHERE p.content LIKE CONCAT('%', :keyword, '%')
		    AND p.status = 'approved'
		    AND p.isDeleted = false
		    AND p.visibility = 'public'
		""")
		List<Posts> searchPublicPostsByContent(@Param("keyword") String keyword);
}