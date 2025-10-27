package aloute.com.service;

import aloute.com.entity.*;
import aloute.com.repository.GroupsUTERepository;
import aloute.com.repository.UserGroupRepository;
import aloute.com.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupsUTERepository groupsUTERepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendService friendService; // ⬅️ THÊM DÒNG NÀY
    @Autowired private GroupMessageRepository groupMessageRepository;

    public GroupsUTE createGroup(String name, MultipartFile avatarFile, User creator, List<User> members) {
        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarUrl = fileStorageService.saveFile(avatarFile);
        }

        GroupsUTE group = new GroupsUTE();
        group.setNameGroup(name);
        group.setAvatar(avatarUrl);
        group.setCreatedBy(creator);
        groupsUTERepository.save(group);

        // Thêm người tạo vào nhóm
        UserGroup creatorUG = new UserGroup();
        creatorUG.setGroup(group);
        creatorUG.setUser(creator);
        creatorUG.setRoleInGroup("admin");
        userGroupRepository.save(creatorUG);

        // Thêm các thành viên được chọn
        for (User u : members) {
            UserGroup ug = new UserGroup();
            ug.setGroup(group);
            ug.setUser(u);
            ug.setRoleInGroup("member");
            userGroupRepository.save(ug);
        }

        return group;
    }

    public List<GroupsUTE> getGroupsByUserId(Integer userId) {
        return groupsUTERepository.findByMembersUserUserId(userId);
    }
    public void addMembers(Integer groupId, List<Integer> memberIds) {
        GroupsUTE group = groupsUTERepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

        for (Integer id : memberIds) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            UserGroup ug = new UserGroup();
            ug.setGroup(group);
            ug.setUser(user);
            ug.setRoleInGroup("member");
            userGroupRepository.save(ug);
        }
    }

	public GroupsUTE findById(Integer groupId) {
		return groupsUTERepository.findById(groupId).orElseThrow();
	}
	public void updateGroupInfo(Integer groupId, User admin, String newName, MultipartFile newAvatar) {
	    GroupsUTE group = groupsUTERepository.findById(groupId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

	    // ✅ Kiểm tra quyền admin
	    if (!group.getCreatedBy().getUserId().equals(admin.getUserId())) {
	        throw new RuntimeException("Bạn không có quyền chỉnh sửa nhóm này");
	    }

	    // ✅ Cập nhật tên nhóm
	    if (newName != null && !newName.isBlank()) {
	        group.setNameGroup(newName);
	    }

	    // ✅ Cập nhật avatar nếu có
	    if (newAvatar != null && !newAvatar.isEmpty()) {
	        try {
	            String avatarUrl = fileStorageService.saveFile(newAvatar);
	            group.setAvatar(avatarUrl);
	        } catch (Exception e) {
	            throw new RuntimeException("Lỗi khi lưu avatar mới", e);
	        }
	    }

	    groupsUTERepository.save(group);
	}
	public void removeMember(Integer groupId, User admin, Integer memberId) {
	    GroupsUTE group = groupsUTERepository.findById(groupId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

	    // ✅ Kiểm tra quyền admin
	    if (!group.getCreatedBy().getUserId().equals(admin.getUserId())) {
	        throw new RuntimeException("Bạn không có quyền loại thành viên khỏi nhóm này");
	    }

	    // ❌ Không cho phép admin tự xóa chính mình
	    if (admin.getUserId().equals(memberId)) {
	        throw new RuntimeException("Không thể tự loại chính mình khỏi nhóm");
	    }

	    // ✅ Tìm và xóa UserGroup tương ứng
	    UserGroup memberUG = userGroupRepository.findByGroupGroupIdAndUserUserId(groupId, memberId)
	            .orElseThrow(() -> new RuntimeException("Thành viên không tồn tại trong nhóm"));

	    userGroupRepository.delete(memberUG);
	}
	@Transactional(readOnly = true) // Thêm @Transactional để tối ưu
    public boolean isUserInGroup(Integer userId, Integer groupId) {
        // Gọi thẳng hàm repository đã tạo ở bước 1
        // Hàm này trả về true nếu tìm thấy, false nếu không
        return userGroupRepository.existsByUser_UserIdAndGroup_GroupId(userId, groupId);
    }
	@Transactional(readOnly = true) // Dùng readOnly vì đây là hàm chỉ đọc
    public List<User> getGroupMembers(Integer groupId) {
        
        // 1. Lấy danh sách các bản ghi "UserGroup" (bảng trung gian)
        List<UserGroup> userGroups = userGroupRepository.findByGroup_GroupId(groupId);

        // 2. Dùng Stream API để trích xuất đối tượng User từ mỗi bản ghi
        return userGroups.stream()
                .map(UserGroup::getUser) // Tương đương: userGroup -> userGroup.getUser()
                .collect(Collectors.toList());
    }
	@Transactional(readOnly = true)
	public List<User> getFriendsNotInGroup(Integer currentUserId, Integer groupId) {
	    
	    // 1. Lấy toàn bộ bạn bè (Giả sử FriendService có hàm này)
	    List<User> allFriends = friendService.getFriendList(currentUserId); 

	    // 2. Lấy toàn bộ thành viên đã có trong nhóm (Dùng hàm bạn đã có)
	    List<User> groupMembers = this.getGroupMembers(groupId);

	    // 3. Lấy ra ID của các thành viên để lọc cho nhanh
	    // (Dùng Set để kiểm tra 'contains' hiệu quả hơn List)
	    java.util.Set<Integer> memberIds = groupMembers.stream()
	                                    .map(User::getUserId)
	                                    .collect(Collectors.toSet());

	    // 4. Lọc và trả về
	    // Lặp qua danh sách BẠN BÈ, chỉ giữ lại ai KHÔNG CÓ ID trong Set thành viên
	    List<User> friendsNotInGroup = allFriends.stream()
	            .filter(friend -> !memberIds.contains(friend.getUserId()))
	            .collect(Collectors.toList());

	    return friendsNotInGroup;
	}
}
