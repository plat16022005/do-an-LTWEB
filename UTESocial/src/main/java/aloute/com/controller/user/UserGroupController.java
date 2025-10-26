package aloute.com.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import aloute.com.dto.GroupInfoDTO;
import aloute.com.dto.MemberDTO;
import aloute.com.entity.GroupsUTE;
import aloute.com.entity.User;
import aloute.com.repository.UserGroupRepository;
import aloute.com.repository.UserRepository;
import aloute.com.service.GroupService;
import aloute.com.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/group")
public class UserGroupController {
    @Autowired private GroupService groupService;
    @Autowired private UserService userService;
    @Autowired private UserGroupRepository userGroupRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping("/create")
    public String createGroup(
            @RequestParam("groupName") String groupName,
            @RequestParam(value = "groupAvatarFile", required = false) MultipartFile groupAvatarFile,
            @RequestParam("memberIds") List<Integer> memberIds,
            HttpSession session
    ) {
        User creator = (User) session.getAttribute("user");
        List<User> members = memberIds.stream()
                .map(userService::findById)
                .collect(Collectors.toList());

        groupService.createGroup(groupName, groupAvatarFile, creator, members);
        return "redirect:/message"; // Sau khi tạo xong reload lại trang tin nhắn
    }
    @PostMapping("/add-members")
    @ResponseBody
    public ResponseEntity<?> addMembersToGroup(@RequestParam Integer groupId,
                                               @RequestParam List<Integer> memberIds) {
        groupService.addMembers(groupId, memberIds);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{groupId}/members")
    @ResponseBody
    public List<Map<String, Object>> getGroupMembers(@PathVariable Integer groupId) {
        return userGroupRepository.findByGroupGroupIdWithUser(groupId)
                .stream()
                .map(ug -> {
                    Map<String, Object> member = new HashMap<>();
                    member.put("userId", ug.getUser().getUserId());
                    member.put("fullName", ug.getUser().getFullName());
                    return member;
                })
                .toList();
    }
    @GetMapping("/{groupId}/not-members")
    @ResponseBody
    public List<User> getFriendsNotInGroup(
            @PathVariable Integer groupId,
            HttpSession session) {

    	User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return List.of(); // Trả về rỗng nếu chưa đăng nhập
        }
        
        // ✅ Gọi hàm service MỚI mà bạn vừa tạo
        return groupService.getFriendsNotInGroup(currentUser.getUserId(), groupId);
    }
    @Transactional
    @GetMapping("/{groupId}/info")
    @ResponseBody
    public Map<String, Object> getGroupInfo(@PathVariable Integer groupId, HttpSession session) {
        GroupsUTE group = groupService.findById(groupId);
        User currentUser = (User) session.getAttribute("user");

        GroupInfoDTO dto = new GroupInfoDTO();
        dto.setGroupId(group.getGroupId());
        dto.setNameGroup(group.getNameGroup());
        dto.setAvatar(group.getAvatar());
        dto.setCreatedById(group.getCreatedBy().getUserId());
        dto.setCreatedByName(group.getCreatedBy().getFullName());
        dto.setAdmin(group.getCreatedBy().getUserId().equals(currentUser.getUserId()));

        List<MemberDTO> members = group.getMembers().stream().map(ug -> {
            MemberDTO m = new MemberDTO();
            m.setUserId(ug.getUser().getUserId());
            m.setFullName(ug.getUser().getFullName());
            m.setAvatarUrl(ug.getUser().getAvatarUrl());
            return m;
        }).toList();

        dto.setMembers(members);

        Map<String, Object> result = new HashMap<>();
        result.put("group", dto);
        result.put("members", members);
        result.put("isAdmin", dto.isAdmin());
        return result;
    }
    @PostMapping("/{groupId}/update")
    @ResponseBody
    public ResponseEntity<?> updateGroupInfo(
            @PathVariable Integer groupId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) MultipartFile groupAvatar,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        groupService.updateGroupInfo(groupId, user, groupName, groupAvatar);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/remove-member")
    @ResponseBody
    public ResponseEntity<?> removeMember(
            @PathVariable Integer groupId,
            @RequestParam Integer memberId,
            HttpSession session
    ) {
        User admin = (User) session.getAttribute("user");
        groupService.removeMember(groupId, admin, memberId);
        return ResponseEntity.ok().build();
    }

}
