package aloute.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aloute.com.entity.BlockedUser;
import aloute.com.entity.User;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Integer> {

    // Kiểm tra xem user1 có chặn user2 không
    @Query("SELECT b FROM BlockedUser b WHERE b.blocker.userId = :blockerId AND b.blocked.userId = :blockedId")
    Optional<BlockedUser> findByBlockerAndBlocked(@Param("blockerId") Integer blockerId, 
                                                    @Param("blockedId") Integer blockedId);

    // Kiểm tra xem có mối quan hệ chặn giữa 2 user không (bất kể ai chặn ai)
    @Query("SELECT b FROM BlockedUser b WHERE " +
           "(b.blocker.userId = :userId1 AND b.blocked.userId = :userId2) OR " +
           "(b.blocker.userId = :userId2 AND b.blocked.userId = :userId1)")
    Optional<BlockedUser> findBlockRelationship(@Param("userId1") Integer userId1, 
                                                 @Param("userId2") Integer userId2);

    // Lấy danh sách người dùng mà user đã chặn
    @Query("SELECT b.blocked FROM BlockedUser b WHERE b.blocker.userId = :userId")
    List<User> findBlockedUsersByBlocker(@Param("userId") Integer userId);

    // Lấy danh sách user ID mà user đã chặn (để filter nhanh)
    @Query("SELECT b.blocked.userId FROM BlockedUser b WHERE b.blocker.userId = :userId")
    List<Integer> findBlockedUserIdsByBlocker(@Param("userId") Integer userId);

    // Lấy danh sách user ID đã chặn user hiện tại (để filter nhanh)
    @Query("SELECT b.blocker.userId FROM BlockedUser b WHERE b.blocked.userId = :userId")
    List<Integer> findBlockerUserIdsByBlocked(@Param("userId") Integer userId);

    // Xóa mối quan hệ chặn
    void deleteByBlockerUserIdAndBlockedUserId(Integer blockerId, Integer blockedId);

    // Đếm số người user đã chặn
    long countByBlockerUserId(Integer userId);
}
