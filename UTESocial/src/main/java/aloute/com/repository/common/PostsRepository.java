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
<<<<<<< Updated upstream
=======
		    WHERE p.visibility = 'public'
		      AND p.isDeleted = false
		      AND p.status = 'approved'
		""")
        List<Posts> findAllWithUserAndAttachments();

	@Query("""
		    SELECT DISTINCT p
		    FROM Posts p
		    LEFT JOIN FETCH p.user u
		    LEFT JOIN FETCH p.attachments
		    WHERE p.isDeleted = false
		      AND p.status = 'approved'
		      AND (
		           p.visibility = 'public'
		           OR (
		                p.visibility = 'friends'
		                AND EXISTS (
		                    SELECT 1 FROM Friend f
		                    WHERE f.status = 'accepted'
		                      AND (
		                          (f.user1.userId = :currentUserId AND f.user2.userId = u.userId)
		                          OR
		                          (f.user2.userId = :currentUserId AND f.user1.userId = u.userId)
		                      )
		                )
		          )
		          OR (
		                p.visibility = 'private'
		                AND u.userId = :currentUserId
		          )
		      )
		      AND NOT EXISTS (
		          SELECT 1 FROM BlockedUser b
		          WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = u.userId)
		             OR (b.blocker.userId = u.userId AND b.blocked.userId = :currentUserId)
		      )
>>>>>>> Stashed changes
		    ORDER BY FUNCTION('RAND')
		""")
        List<Posts> findAllWithUserAndAttachments();
	@Query("""
<<<<<<< Updated upstream
		    SELECT DISTINCT p FROM Posts p
		    LEFT JOIN FETCH p.user u
		    LEFT JOIN FETCH p.attachments
		    WHERE u.userId = :userId
=======
		    SELECT DISTINCT p 
		    FROM Posts p
		    LEFT JOIN FETCH p.user u
		    LEFT JOIN FETCH p.attachments
		    WHERE p.isDeleted = false
		      AND p.status = 'approved'
		      AND p.visibility IN ('public', 'friends')
		      AND p.user.userId IN (
		        SELECT CASE
		                   WHEN f.user1.userId = :currentUserId THEN f.user2.userId
		                   ELSE f.user1.userId
		               END
		        FROM Friend f
		        WHERE (f.user1.userId = :currentUserId OR f.user2.userId = :currentUserId)
		          AND f.status = 'Accepted'
		    )
		      AND NOT EXISTS (
		          SELECT 1 FROM BlockedUser b
		          WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = u.userId)
		             OR (b.blocker.userId = u.userId AND b.blocked.userId = :currentUserId)
		      )
		    ORDER BY p.createdAt DESC
		""")
        List<Posts> findFriendPosts(@Param("currentUserId") Integer currentUserId);

	@Query("""
		    SELECT DISTINCT p
		    FROM Posts p
		    LEFT JOIN FETCH p.user u
		    LEFT JOIN FETCH p.attachments
		    WHERE p.isDeleted = false
		      AND p.status = 'approved'
		      AND (

		          u.userId = :targetUserId AND :currentUserId = :targetUserId
		          

		          OR (
		              u.userId = :targetUserId
		              AND p.visibility IN ('public', 'friends')
		              AND EXISTS (
		                  SELECT 1 FROM Friend f
		                  WHERE f.status = 'Accepted'
		                    AND (
		                        (f.user1.userId = :currentUserId AND f.user2.userId = :targetUserId)
		                        OR
		                        (f.user2.userId = :currentUserId AND f.user1.userId = :targetUserId)
		                    )
		              )
		          )


		          OR (
		              u.userId = :targetUserId
		              AND p.visibility = 'public'
		          )
		      )
		      AND NOT EXISTS (
		          SELECT 1 FROM BlockedUser b
		          WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = :targetUserId)
		             OR (b.blocker.userId = :targetUserId AND b.blocked.userId = :currentUserId)
		      )
>>>>>>> Stashed changes
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