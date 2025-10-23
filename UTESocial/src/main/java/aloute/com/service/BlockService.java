package aloute.com.service;

import aloute.com.entity.BlockedUser;
import aloute.com.entity.User;
import aloute.com.repository.BlockedUserRepository;
import aloute.com.repository.FriendRepository;
import aloute.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý nghiệp vụ chặn người dùng
 */
@Service
public class BlockService {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private aloute.com.repository.SharesRepository sharesRepository;

    /**
     * Chặn người dùng
     * 
     * @param blockerId ID của người chặn
     * @param blockedId ID của người bị chặn
     * @return Message thông báo kết quả
     */
    @Transactional
    public String blockUser(Integer blockerId, Integer blockedId) {
        // Validate: không tự chặn mình
        if (blockerId.equals(blockedId)) {
            return "Không thể tự chặn chính mình";
        }

        // Lấy thông tin user
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User blocked = userRepository.findById(blockedId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng bị chặn"));

        // Validate: không cho phép chặn nếu đang là bạn bè
        boolean isFriend = friendRepository.isFriend(blockerId, blockedId);
        if (isFriend) {
            return "Không thể chặn người đang là bạn bè. Vui lòng hủy kết bạn trước.";
        }

        // Validate: kiểm tra đã có ai chặn ai chưa (chỉ cho phép 1 người chặn)
        boolean hasBlockRelationship = blockedUserRepository.existsBlockRelationship(blockerId, blockedId);
        if (hasBlockRelationship) {
            return "Đã tồn tại mối quan hệ chặn giữa hai người dùng này";
        }

        // XÓA TẤT CẢ shares của blocked từ bài viết của blocker
        long deletedSharesCount = sharesRepository.countSharesByBlockedUserFromBlockerPosts(blockerId, blockedId);
        if (deletedSharesCount > 0) {
            sharesRepository.deleteSharesByBlockedUserFromBlockerPosts(blockerId, blockedId);
            System.out.println("✅ Đã xóa " + deletedSharesCount + " bài đăng lại của user " + blockedId + " từ bài viết của user " + blockerId);
        }

        // Tạo mới BlockedUser
        BlockedUser blockedUser = new BlockedUser(blocker, blocked);
        blockedUserRepository.save(blockedUser);

        return "Đã chặn người dùng thành công";
    }

    /**
     * Bỏ chặn người dùng
     * 
     * @param blockerId ID của người chặn
     * @param blockedId ID của người bị chặn
     * @return Message thông báo kết quả
     */
    @Transactional
    public String unblockUser(Integer blockerId, Integer blockedId) {
        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User blocked = userRepository.findById(blockedId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng bị chặn"));

        // Kiểm tra có mối quan hệ chặn không
        if (!blockedUserRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            return "Bạn chưa chặn người dùng này";
        }

        // Xóa mối quan hệ chặn
        blockedUserRepository.deleteByBlockerAndBlocked(blocker, blocked);

        return "Đã bỏ chặn người dùng thành công";
    }

    /**
     * Kiểm tra blocker có đang chặn blocked không (CHỈ MỘT CHIỀU)
     * 
     * @param blockerId ID của người chặn
     * @param blockedId ID của người bị chặn
     * @return true nếu blocker đang chặn blocked
     */
    public boolean isBlockedByMe(Integer blockerId, Integer blockedId) {
        if (blockerId == null || blockedId == null) {
            return false;
        }
        User blocker = userRepository.findById(blockerId).orElse(null);
        User blocked = userRepository.findById(blockedId).orElse(null);
        if (blocker == null || blocked == null) {
            return false;
        }
        return blockedUserRepository.existsByBlockerAndBlocked(blocker, blocked);
    }
    
    /**
     * Kiểm tra currentUser có bị targetUser chặn không
     * 
     * @param currentUserId ID user hiện tại
     * @param targetUserId ID user target
     * @return true nếu currentUser bị targetUser chặn
     */
    public boolean isBlockedByThem(Integer currentUserId, Integer targetUserId) {
        if (currentUserId == null || targetUserId == null) {
            return false;
        }
        User current = userRepository.findById(currentUserId).orElse(null);
        User target = userRepository.findById(targetUserId).orElse(null);
        if (current == null || target == null) {
            return false;
        }
        // Kiểm tra target có chặn current không
        return blockedUserRepository.existsByBlockerAndBlocked(target, current);
    }

    /**
     * Kiểm tra có mối quan hệ chặn giữa 2 user không (không phân biệt ai chặn ai)
     * 
     * @param userId1 ID user thứ nhất
     * @param userId2 ID user thứ hai
     * @return true nếu có mối quan hệ chặn
     */
    public boolean hasBlockRelationship(Integer userId1, Integer userId2) {
        if (userId1 == null || userId2 == null) {
            return false;
        }
        return blockedUserRepository.existsBlockRelationship(userId1, userId2);
    }

    /**
     * Lấy danh sách người dùng đã chặn
     * 
     * @param blockerId ID của người chặn
     * @return Danh sách User đã bị chặn
     */
    public List<User> getBlockedUsers(Integer blockerId) {
        return blockedUserRepository.findBlockedUsersByBlocker(blockerId);
    }

    /**
     * Lấy danh sách ID của người dùng đã chặn (tối ưu cho filter)
     * 
     * @param blockerId ID của người chặn
     * @return Danh sách ID
     */
    public List<Integer> getBlockedUserIds(Integer blockerId) {
        return blockedUserRepository.getBlockedUserIds(blockerId);
    }

    /**
     * Lấy danh sách ID của tất cả user có mối quan hệ chặn với currentUser
     * (bao gồm user đã chặn currentUser và currentUser đã chặn)
     * 
     * @param currentUserId ID của user hiện tại
     * @return Danh sách ID
     */
    public List<Integer> getAllBlockedRelationshipUserIds(Integer currentUserId) {
        return blockedUserRepository.getAllBlockedRelationshipUserIds(currentUserId);
    }

    /**
     * Đếm số lượng người dùng đã chặn
     * 
     * @param userId ID của user
     * @return Số lượng
     */
    public long countBlockedUsers(Integer userId) {
        return blockedUserRepository.countByBlocker(userId);
    }
}
