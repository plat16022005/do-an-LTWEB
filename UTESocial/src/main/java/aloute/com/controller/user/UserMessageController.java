package aloute.com.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import aloute.com.dto.AttachmentDTO;
import aloute.com.dto.MessageDTO;
import aloute.com.entity.Message;
import aloute.com.entity.Attachments;
import aloute.com.entity.GroupMessage;
import aloute.com.entity.GroupsUTE;
import aloute.com.entity.User;
import aloute.com.repository.UserGroupRepository;
import aloute.com.repository.UserRepository;
import aloute.com.service.FriendService;
import aloute.com.service.GroupMessageService;
import aloute.com.service.GroupService;
import aloute.com.service.MessageService;
import aloute.com.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserMessageController {
	@Autowired
	private GroupService groupService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MessageService messageService;
	@Autowired
	private GroupMessageService groupMessageService;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	// (Hãy chắc chắn rằng bạn đã tiêm @Autowired MessageService ở đầu Controller)

	@GetMapping("/message")
	public String showMessageForm(Model model, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        // (Lỗi chính tả: Sửa "access-deniel" thành "access-denied" nếu bạn muốn)
	        return "redirect:/access-deniel"; 
	    }

	    // ▼▼▼ THAY ĐỔI TỪ ĐÂY ▼▼▼

	    // 1. Lấy danh sách người đã chat (thay vì toàn bộ bạn bè)
	    List<User> chatList = messageService.getConversationPartners(user.getUserId());

	    // 2. Map tin nhắn mới nhất
	    Map<Integer, String> lastMessages = new HashMap<>();
	    for (User partner : chatList) { // Lặp qua danh sách người đã chat
	        String preview = messageService.getLatestMessagePreview(user.getUserId(), partner.getUserId());
	        lastMessages.put(partner.getUserId(), preview);
	    }

	    // 3. Đưa danh sách người đã chat ra model
	    model.addAttribute("chatList", chatList); // Đổi tên từ "friends" -> "chatList"
	    model.addAttribute("lastMessages", lastMessages);
	    List<User> friends = friendService.getFriendList(user.getUserId());
	    model.addAttribute("friends", friends);
	    List<GroupsUTE> groupList = groupService.getGroupsByUserId(user.getUserId());
	    model.addAttribute("groupList", groupList);
	    // ▲▲▲ THAY ĐỔI ĐẾN ĐÂY ▲▲▲

	    return "user/message";
	}
	@GetMapping("/message/load/{friendId}")
	@ResponseBody
	public List<Message> loadMessages(@PathVariable Integer friendId, HttpSession session) {
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return List.of();
	    }
	    return messageService.getAllMessagesBetween(currentUser.getUserId(), friendId);
	}
	@GetMapping("/message/{friendId}")
	public String openMessagePage(@PathVariable(required = false) Integer friendId, Model model, HttpSession session) {
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return "redirect:/login";
	    }

	    // 1. Lấy danh sách người đã chat (sắp xếp theo tin nhắn mới nhất)
	    List<User> chatList = messageService.getConversationPartners(currentUser.getUserId());

	    // 2. ⭐ LOGIC MỚI QUAN TRỌNG ⭐
	    // Kiểm tra xem người muốn chat (friendId) đã có trong danh sách chưa
	    if (friendId != null) {
	        boolean isAlreadyInList = chatList.stream()
	                                          .anyMatch(user -> user.getUserId().equals(friendId));

	        // Nếu CHƯA CÓ trong danh sách (đây là tin nhắn mới)
	        if (!isAlreadyInList) {
	            // Lấy thông tin của người đó từ DB
	            User userToChat = userRepository.findById(friendId).orElse(null); 
	            // (Hoặc dùng: userService.findUserById(friendId))

	            if (userToChat != null) {
	                // Thêm người đó vào ĐẦU danh sách để hiển thị
	                chatList.add(0, userToChat);
	            }
	        }
	    }

	    // 3. Lấy tin nhắn cuối cùng (như cũ)
	    Map<Integer, String> lastMessages = new HashMap<>();
	    for (User partner : chatList) {
	        String preview = messageService.getLatestMessagePreview(currentUser.getUserId(), partner.getUserId());
	        
	        // Nếu preview là null (vì là chat mới), đặt là chuỗi rỗng
	        lastMessages.put(partner.getUserId(), (preview != null ? preview : ""));
	    }

	    // 4. Đưa ra Model (như cũ)
	    model.addAttribute("chatList", chatList);
	    model.addAttribute("lastMessages", lastMessages);
	    model.addAttribute("friendId", friendId); // Rất quan trọng để JS auto-open
	    List<GroupsUTE> groupList = groupService.getGroupsByUserId(currentUser.getUserId());
	    model.addAttribute("groupList", groupList);
	    return "user/message";
	}
	@PostMapping("/message/send")
	@ResponseBody
	public Map<String, Object> sendMessage(
	        @RequestParam("content") String content,
	        @RequestParam("receiverId") Integer receiverId,
	        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
	        HttpSession session
	) {
	    User currentUser = (User) session.getAttribute("user");
	    Message saved = messageService.saveMessage(currentUser.getUserId(), receiverId, content, attachments);

	    // ✅ Tạo object sender để client đọc msg.sender.fullName không bị undefined
	    Map<String, Object> sender = new HashMap<>();
	    sender.put("userId", saved.getSender().getUserId());
	    sender.put("fullName", saved.getSender().getFullName());
	    sender.put("avatarUrl", saved.getSender().getAvatarUrl());

	    Map<String, Object> receiver = new HashMap<>();
	    receiver.put("userId", saved.getReceiver().getUserId());

	    // ✅ Đính attachments
	    List<Map<String, Object>> attachmentList = saved.getAttachments() != null
	            ? saved.getAttachments().stream().map(att -> {
	                Map<String, Object> a = new HashMap<>();
	                a.put("fileUrl", att.getFileUrl());
	                a.put("fileType", att.getFileType());
	                return a;
	            }).toList()
	            : List.of();

	    Map<String, Object> response = new HashMap<>();
	    response.put("messageId", saved.getMessageId());
	    response.put("content", saved.getContent());
	    response.put("createdAt", saved.getCreatedAt());
	    response.put("sender", sender);
	    response.put("receiver", receiver);
	    response.put("attachments", attachmentList);

	    // ⭐ CHỈNH SỬA QUAN TRỌNG: Thêm dòng này ⭐
	    // Báo cho JS biết đây là tin nhắn 1-1 (không phải nhóm)
	    response.put("groupId", null);

	    // ✅ Gửi đúng cấu trúc client đang subscribe
	    messagingTemplate.convertAndSend("/topic/messages/" + receiverId, response);
	    messagingTemplate.convertAndSend("/topic/messages/" + currentUser.getUserId(), response);

	    return response;
	}
	@GetMapping("/group/messages/{groupId}")
	@ResponseBody
	public List<Map<String, Object>> loadGroupMessages(@PathVariable Integer groupId, HttpSession session) {
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return List.of(); 
	    }

	    // (Nên kiểm tra xem user có trong nhóm này không)
	    if (!groupService.isUserInGroup(currentUser.getUserId(), groupId)) {
	    	// (Giả sử bạn có hàm này trong GroupService)
	        return List.of(); 
	    }

	    // 1. Lấy danh sách tin nhắn nhóm từ service
	    List<GroupMessage> messages = groupMessageService.getMessagesByGroupId(groupId);

	    // 2. Chuyển đổi sang cấu trúc JSON thống nhất mà front-end có thể hiểu
	    return messages.stream()
	            .map(this::convertGroupMessageToMap) // Dùng hàm helper bên dưới
	            .collect(Collectors.toList());
	}

	/**
	 * Gửi một tin nhắn mới vào nhóm.
	 * Được gọi bởi sendMessage() trong JS.
	 */
	@PostMapping("/group/send-message")
	@ResponseBody
	public Map<String, Object> sendGroupMessage(
	        @RequestParam("content") String content,
	        @RequestParam("groupId") Integer groupId, 
	        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
	        HttpSession session
	) {
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return Map.of("error", "User not authenticated");
	    }

	    // 1. Lưu tin nhắn nhóm (dùng service mới)
	    // (Hàm này cần trả về GroupMessage đã lưu, bao gồm sender, group, attachments)
	    GroupMessage saved = groupMessageService.saveGroupMessage(currentUser, groupId, content, attachments);

	    // 2. Chuyển đổi sang cấu trúc JSON thống nhất
	    Map<String, Object> response = convertGroupMessageToMap(saved);

	    // 3. Gửi WebSocket đến TẤT CẢ thành viên nhóm
	    // (Bạn cần có hàm getGroupMembers trong GroupService)
	    List<User> members = groupService.getGroupMembers(groupId); 
	    
	    for (User member : members) {
	        // Gửi đến topic cá nhân của từng người
	        messagingTemplate.convertAndSend("/topic/messages/" + member.getUserId(), response);
	    }

	    // 4. Trả về cho người gửi (để JS renderMessage ngay lập tức)
	    return response;
	}

	/**
	 * HÀM HELPER: Chuyển đổi GroupMessage sang Map JSON
	 * để thống nhất với cấu trúc của tin nhắn 1-1.
	 */
	private Map<String, Object> convertGroupMessageToMap(GroupMessage msg) {
	    // 1. Tạo thông tin sender
	    Map<String, Object> sender = new HashMap<>();
	    sender.put("userId", msg.getSender().getUserId());
	    sender.put("fullName", msg.getSender().getFullName());
	    sender.put("avatarUrl", msg.getSender().getAvatarUrl());

	    // 2. Tạo danh sách attachments
	    List<Map<String, Object>> attachmentList = msg.getAttachments() != null
	            ? msg.getAttachments().stream().map(att -> {
	                Map<String, Object> a = new HashMap<>();
	                // Tên cột của bạn là 'fileURL' (chữ L hoa)
	                // JS (renderMessage) mong muốn 'fileUrl'
	                // (Nếu att.getFileURL() là đúng)
	                a.put("fileUrl", att.getFileURL()); 
	                a.put("fileType", att.getFileType());
	                return a;
	            }).toList()
	            : List.of();

	    // 3. Tạo đối tượng response chính
	    Map<String, Object> response = new HashMap<>();
	    response.put("messageId", msg.getGroupMessageId()); // ⬅️ Dùng ID của tin nhắn nhóm
	    response.put("content", msg.getContent());
	    response.put("createdAt", msg.getCreatedAt());
	    response.put("sender", sender);
	    response.put("receiver", null); // ⬅️ Tin nhắn nhóm không có receiver cụ thể
	    response.put("attachments", attachmentList);
	    response.put("groupId", msg.getGroup().getGroupId()); // ⬅️ Đây là mấu chốt

	    return response;
	}
}
