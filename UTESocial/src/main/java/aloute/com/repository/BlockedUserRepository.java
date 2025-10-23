package aloute.com.repository;

import aloute.com.entity.BlockedUser;
import aloute.com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository xử lý các thao tác với BlockedUser
 */
@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Integer> {

    /**
     * Tìm mối quan hệ chặn cụ thể: blocker chặn blocked
     */
    Optional<BlockedUser> findByBlockerAndBlocked(User blocker, User blocked);

    /**
     * Kiểm tra xem blocker đã chặn blocked chưa
     */
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    /**
     * Tìm mối quan hệ chặn giữa 2 user (không phân biệt ai chặn ai)
     * Trả về true nếu có bất kỳ mối quan hệ chặn nào
     */
    @Query("SELECT COUNT(b) > 0 FROM BlockedUser b " +
           "WHERE (b.blocker.userId = :userId1 AND b.blocked.userId = :userId2) " +
           "OR (b.blocker.userId = :userId2 AND b.blocked.userId = :userId1)")
    boolean existsBlockRelationship(@Param("userId1") Integer userId1, 
                                     @Param("userId2") Integer userId2);

    /**
     * Lấy danh sách tất cả người dùng mà blocker đã chặn
     */
    @Query("SELECT b.blocked FROM BlockedUser b WHERE b.blocker.userId = :blockerId")
    List<User> findBlockedUsersByBlocker(@Param("blockerId") Integer blockerId);

    /**
     * Lấy danh sách ID của tất cả user bị chặn bởi blocker
     * (Tối ưu cho việc filter nhanh)
     */
    @Query("SELECT b.blocked.userId FROM BlockedUser b WHERE b.blocker.userId = :blockerId")
    List<Integer> getBlockedUserIds(@Param("blockerId") Integer blockerId);

    /**
     * Lấy danh sách ID của tất cả user có mối quan hệ chặn với currentUser
     * (bao gồm cả user đã chặn currentUser và currentUser đã chặn)
     */
    @Query("SELECT CASE " +
           "WHEN b.blocker.userId = :currentUserId THEN b.blocked.userId " +
           "ELSE b.blocker.userId END " +
           "FROM BlockedUser b " +
           "WHERE b.blocker.userId = :currentUserId OR b.blocked.userId = :currentUserId")
    List<Integer> getAllBlockedRelationshipUserIds(@Param("currentUserId") Integer currentUserId);

    /**
     * Xóa mối quan hệ chặn giữa blocker và blocked
     */
    void deleteByBlockerAndBlocked(User blocker, User blocked);

    /**
     * Đếm số lượng người dùng mà user này đã chặn
     */
    @Query("SELECT COUNT(b) FROM BlockedUser b WHERE b.blocker.userId = :userId")
    long countByBlocker(@Param("userId") Integer userId);
}
