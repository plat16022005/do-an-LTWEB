package aloute.com.repository.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate; 
import java.time.LocalTime;
import java.time.LocalDateTime; 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable;

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
		      AND NOT EXISTS (
		          SELECT 1 FROM BlockedUser b
		          WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = u.userId)
		             OR (b.blocker.userId = u.userId AND b.blocked.userId = :currentUserId)
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
		      AND NOT EXISTS (
		          SELECT 1 FROM BlockedUser b
		          WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = :targetUserId)
		             OR (b.blocker.userId = :targetUserId AND b.blocked.userId = :currentUserId)
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
		    AND NOT EXISTS (
		        SELECT 1 FROM BlockedUser b
		        WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = u.userId)
		           OR (b.blocker.userId = u.userId AND b.blocked.userId = :currentUserId)
		    )
		""")
		List<Posts> searchPublicPostsByContent(@Param("keyword") String keyword, @Param("currentUserId") Integer currentUserId);

	@Query("""
		    SELECT DISTINCT p FROM Posts p
		    LEFT JOIN FETCH p.attachments
		    WHERE p.postId = :postId
		""")
		Optional<Posts> findByIdWithAttachments(@Param("postId") Integer postId);

	@Query("SELECT p FROM Posts p JOIN FETCH p.user LEFT JOIN FETCH p.attachments WHERE p.postId = :id")
	Optional<Posts> findPostWithUser(@Param("id") Integer id);

	
	//Tìm Post theo ID và fetch thông tin User liên quan.
	@Query("SELECT p FROM Posts p LEFT JOIN FETCH p.user WHERE p.postId = :postId")
    Optional<Posts> findByIdWithUser(@Param("postId") Integer postId);
	
	//Đếm số lượng bài đăng theo trạng thái cụ thể và chưa bị xóa.
	@Query("SELECT COUNT(p) FROM Posts p WHERE p.status = :status AND p.isDeleted = false")
    long countByStatusAndIsDeletedFalse(@Param("status") String status);
	
	//Lấy danh sách Post và lọc post
	@Query("SELECT DISTINCT p FROM Posts p LEFT JOIN FETCH p.user u WHERE p.isDeleted = false " +
	           "AND (:keyword IS NULL OR :keyword = '' OR p.content LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.nameUser) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	           "AND (:status IS NULL OR :status = '' OR p.status = :status) " +
	           
	           "AND (CAST(:startDate AS date) IS NULL OR p.createdAt >= :startDateTime) " + // So sánh với đầu ngày startDate
	           "AND (CAST(:endDate AS date) IS NULL OR p.createdAt <= :endDateTime) " +     // So sánh với cuối ngày endDate
	          
	           "ORDER BY p.createdAt DESC") // Sắp xếp theo ngày tạo mới nhất
		Page<Posts> findPostsWithFiltersForAdmin
	    (
	            @Param("keyword") String keyword,
	            @Param("status") String status,
	            
	            @Param("startDate") LocalDate startDate,
	            @Param("endDate") LocalDate endDate,
	            @Param("startDateTime") LocalDateTime startDateTime, 
	            @Param("endDateTime") LocalDateTime endDateTime,
	            Pageable pageable
	    );
	
	default Page<Posts> findPostsWithFiltersForAdmin(String keyword, String status, LocalDate startDate, LocalDate endDate, Pageable pageable) 
	{ 
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        // Gọi query chính với Pageable
        return findPostsWithFiltersForAdmin(keyword, status, startDate, endDate, startDateTime, endDateTime, pageable); // Truyền Pageable
    }

	@Query("SELECT DISTINCT p FROM Posts p LEFT JOIN FETCH p.user WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Posts> findAllWithUserForAdmin();
//	//Lấy danh sách Post và lọc post
//	@Query("SELECT DISTINCT p FROM Posts p LEFT JOIN FETCH p.user u WHERE p.isDeleted = false " +
//			"AND (:keyword IS NULL OR :keyword = '' OR p.content LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.nameUser) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
//			"AND (:status IS NULL OR :status = '' OR p.status = :status) " +
//			
//			"AND (CAST(:startDate AS date) IS NULL OR p.createdAt >= :startDateTime) " + // So sánh với đầu ngày startDate
//			"AND (CAST(:endDate AS date) IS NULL OR p.createdAt <= :endDateTime) "   // So sánh với cuối ngày endDate
//			) 
//		Page<Posts> findPostsWithFiltersForAdmin
//		(
//				@Param("keyword") String keyword,
//				@Param("status") String status,
//				
//				@Param("startDate") LocalDate startDate,
//				@Param("endDate") LocalDate endDate,
//				@Param("startDateTime") LocalDateTime startDateTime, 
//				@Param("endDateTime") LocalDateTime endDateTime,
//				Pageable pageable
//		);
//	default Page<Posts> findPostsWithFiltersForAdmin(String keyword, String status, LocalDate startDate, LocalDate endDate, Pageable pageable) 
//	{ 
//        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
//        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
//        return findPostsWithFiltersForAdmin(keyword, status, startDate, endDate, startDateTime, endDateTime, pageable); // Truyền Pageable
//    }

}