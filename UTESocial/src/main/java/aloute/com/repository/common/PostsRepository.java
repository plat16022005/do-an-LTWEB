package aloute.com.repository.common;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
	@Query("""
		    SELECT DISTINCT p 
		    FROM Posts p
		    LEFT JOIN FETCH p.user
		    LEFT JOIN FETCH p.attachments
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
		    ORDER BY FUNCTION('RAND')
		""")
		List<Posts> findAllVisiblePosts(@Param("currentUserId") Integer currentUserId);
	@Query("""
		    SELECT DISTINCT p 
		    FROM Posts p
		    LEFT JOIN FETCH p.user
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
		    ORDER BY p.createdAt DESC
		""")
		List<Posts> findPostsOfUserWithVisibility(
		        @Param("currentUserId") Integer currentUserId,
		        @Param("targetUserId") Integer targetUserId
		);


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

	@Query("""
		    SELECT DISTINCT p FROM Posts p
		    LEFT JOIN FETCH p.attachments
		    WHERE p.postId = :postId
		""")
		Optional<Posts> findByIdWithAttachments(@Param("postId") Integer postId);
	@Query("SELECT p FROM Posts p JOIN FETCH p.user LEFT JOIN FETCH p.attachments WHERE p.postId = :id")
	Optional<Posts> findPostWithUser(@Param("id") Integer id);
}