package aloute.com.repository;

import aloute.com.entity.Friend;
import aloute.com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    // ✅ Lấy toàn bộ bạn bè đã accepted
    @Query("""
        SELECT f FROM Friend f
        WHERE f.status = 'accepted'
        AND (f.user1.userId = :userId OR f.user2.userId = :userId)
    """)
    List<Friend> findAcceptedFriends(@Param("userId") Integer userId);
    
    @Query("""
    	    SELECT f FROM Friend f
    	    WHERE f.status = 'pending'
    	    AND f.user2.userId = :userId
    	""")
    List<Friend> findWaitingFriends(@Param("userId") Integer userId);
    
    @Query("""
    	    SELECT f FROM Friend f
    	    WHERE f.status = 'pending'
    	    AND f.user1.userId = :userId
    	""")
    List<Friend> findRequestedFriends(@Param("userId") Integer userId);
    
    // ✅ Kiểm tra có phải bạn bè không
    @Query("""
        SELECT COUNT(f) > 0 FROM Friend f
        WHERE f.status = 'accepted'
        AND ((f.user1.userId = :userId AND f.user2.userId = :targetId)
         OR (f.user1.userId = :targetId AND f.user2.userId = :userId))
    """)
    boolean isFriend(@Param("userId") Integer userId, @Param("targetId") Integer targetId);

    // ✅ Kiểm tra lời mời kết bạn đang chờ xử lý
    @Query("""
            SELECT COUNT(f) > 0 FROM Friend f
            WHERE f.status = 'pending'
            AND f.user1.userId = :userId
            AND f.user2.userId = :targetId
        """)
    boolean isPending(@Param("userId") Integer userId, @Param("targetId") Integer targetId);
    
 // ✅ Kiểm tra có đang được người khác gửi lời mời kết bạn hay không
    @Query("""
        SELECT COUNT(f) > 0 FROM Friend f
        WHERE f.status = 'pending'
        AND f.user2.userId = :userId
        AND f.user1.userId = :targetId
    """)
    boolean isBeingRequested(@Param("userId") Integer userId, @Param("targetId") Integer targetId);
    @Query("""
    	    DELETE FROM Friend f
    	    WHERE (f.user1.userId = :userId1 AND f.user2.userId = :userId2)
    	       OR (f.user1.userId = :userId2 AND f.user2.userId = :userId1)
    	""")
    	@org.springframework.transaction.annotation.Transactional
    	@org.springframework.data.jpa.repository.Modifying
    void deleteFriendship(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
    @Query("""
    	    SELECT f FROM Friend f
    	    WHERE f.user1.userId = :userId1
    	      AND f.user2.userId = :userId2
    	""")
    Friend findByUser1AndUser2(
    	        @Param("userId1") Integer userId1,
    	        @Param("userId2") Integer userId2);

}

