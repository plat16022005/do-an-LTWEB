package aloute.com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aloute.com.entity.BlockedUser;
import aloute.com.entity.User;
import aloute.com.repository.BlockedUserRepository;
import aloute.com.repository.UserRepository;

@Service
public class BlockService {

    @Autowired
    private BlockedUserRepository blockedUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendService friendService;

    /**
     * Chặn một người dùng
     * Điều kiện: Không được chặn nếu đã là bạn bè
     * 
     * @param blockerId ID người chặn
     * @param blockedId ID người bị chặn
     * @return true nếu chặn thành công, false nếu không thể chặn
     */
    @Transactional
    public boolean blockUser(Integer blockerId, Integer blockedId) {
        // Không thể tự chặn mình
        if (blockerId.equals(blockedId)) {
            return false;
        }

        // Kiểm tra xem đã là bạn bè chưa - không cho phép chặn nếu đã là bạn bè
        if (friendService.checkFriend(blockerId, blockedId)) {
            return false;
        }

        // Kiểm tra xem đã chặn rồi chưa
        Optional<BlockedUser> existingBlock = blockedUserRepository.findByBlockerAndBlocked(blockerId, blockedId);
        if (existingBlock.isPresent()) {
            return false; // Đã chặn rồi
        }

        // Kiểm tra xem người kia đã chặn mình chưa (chỉ có 1 người chặn được)
        Optional<BlockedUser> reverseBlock = blockedUserRepository.findByBlockerAndBlocked(blockedId, blockerId);
        if (reverseBlock.isPresent()) {
            return false; // Người kia đã chặn mình rồi, không thể chặn ngược lại
        }

        // Tạo mối quan hệ chặn mới
        User blocker = userRepository.findById(blockerId).orElse(null);
        User blocked = userRepository.findById(blockedId).orElse(null);

        if (blocker == null || blocked == null) {
            return false;
        }

        BlockedUser blockRelation = new BlockedUser();
        blockRelation.setBlocker(blocker);
        blockRelation.setBlocked(blocked);
        blockedUserRepository.save(blockRelation);

        return true;
    }

    /**
     * Bỏ chặn người dùng
     * 
     * @param blockerId ID người đã chặn
     * @param blockedId ID người bị chặn
     * @return true nếu bỏ chặn thành công
     */
    @Transactional
    public boolean unblockUser(Integer blockerId, Integer blockedId) {
        Optional<BlockedUser> blockRelation = blockedUserRepository.findByBlockerAndBlocked(blockerId, blockedId);
        
        if (blockRelation.isPresent()) {
            blockedUserRepository.delete(blockRelation.get());
            return true;
        }
        
        return false;
    }

    /**
     * Kiểm tra xem user1 có chặn user2 không
     */
    public boolean isBlocked(Integer blockerId, Integer blockedId) {
        return blockedUserRepository.findByBlockerAndBlocked(blockerId, blockedId).isPresent();
    }

    /**
     * Kiểm tra xem có mối quan hệ chặn giữa 2 user không (bất kể ai chặn ai)
     * Dùng để ẩn bài viết, ẩn trong tìm kiếm, v.v.
     */
    public boolean hasBlockRelationship(Integer userId1, Integer userId2) {
        return blockedUserRepository.findBlockRelationship(userId1, userId2).isPresent();
    }

    /**
     * Lấy danh sách người dùng mà user đã chặn
     */
    public List<User> getBlockedUsers(Integer userId) {
        return blockedUserRepository.findBlockedUsersByBlocker(userId);
    }

    /**
     * Lấy danh sách ID người dùng mà user đã chặn
     */
    public List<Integer> getBlockedUserIds(Integer userId) {
        return blockedUserRepository.findBlockedUserIdsByBlocker(userId);
    }

    /**
     * Lấy danh sách ID người dùng đã chặn user hiện tại
     */
    public List<Integer> getBlockerUserIds(Integer userId) {
        return blockedUserRepository.findBlockerUserIdsByBlocked(userId);
    }

    /**
     * Đếm số người dùng đã chặn
     */
    public long countBlockedUsers(Integer userId) {
        return blockedUserRepository.countByBlockerUserId(userId);
    }

    /**
     * Kiểm tra ai là người chặn
     * @return "none" nếu không có ai chặn, "user1" nếu user1 chặn user2, "user2" nếu user2 chặn user1
     */
    public String getBlocker(Integer userId1, Integer userId2) {
        if (isBlocked(userId1, userId2)) {
            return "user1";
        } else if (isBlocked(userId2, userId1)) {
            return "user2";
        }
        return "none";
    }
}
