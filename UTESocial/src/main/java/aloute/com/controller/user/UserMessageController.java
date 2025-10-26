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
import org.springframework.web.bind.annotation.RequestBody;
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
	        return "redirect:/access-deniel";
	    }

	    // 1. Lấy ID người dùng
	    Integer currentUserId = user.getUserId();

	    // 2. Lấy danh sách (có thể chứa null)
	    List<User> chatList = messageService.getConversationPartners(currentUserId);
	    List<GroupsUTE> groupList = groupService.getGroupsByUserId(currentUserId);
	    List<User> friends = friendService.getFriendList(currentUserId); // Lấy friends ở đây

	    // --- LỌC NULL ---
	    List<User> cleanChatList = chatList.stream()
	                                       .filter(u -> u != null)
	                                       .collect(Collectors.toList());
	    List<GroupsUTE> cleanGroupList = groupList.stream()
	                                              .filter(g -> g != null)
	                                              .collect(Collectors.toList());
	    List<User> cleanFriendsList = friends.stream()
	                                         .filter(f -> f != null)
	                                         .collect(Collectors.toList());
	    // -----------------

	    // 4. Lấy Map tin nhắn cuối (Lặp qua danh sách SẠCH)
	    Map<Object, String> lastMessages = new HashMap<>();
	    for (User partner : cleanChatList) { // Dùng danh sách sạch
	        String preview = messageService.getLatestMessagePreview(currentUserId, partner.getUserId());
	        lastMessages.put(partner.getUserId(), (preview != null ? preview : ""));
	    }
	    // (Bỏ qua preview nhóm)

	    // 5. Lấy số lượng tin nhắn chưa đọc
	    Map<Integer, Long> unreadUserCounts =
	        messageService.getUnreadCountsPerSender(currentUserId);
	    Map<Integer, Long> unreadGroupCounts = new HashMap<>();

	    // 6. Đưa TẤT CẢ (danh sách SẠCH) ra Model
	    model.addAttribute("chatList", cleanChatList);
	    model.addAttribute("groupList", cleanGroupList);
	    model.addAttribute("lastMessages", lastMessages);
	    model.addAttribute("friends", cleanFriendsList); // Dùng danh sách friends đã lọc
	    model.addAttribute("unreadUserCounts", unreadUserCounts);
	    model.addAttribute("unreadGroupCounts", unreadGroupCounts);

	    // 7. Trả về tên view
	    return "user/message"; // Dấu chấm phẩy ở cuối hàm return
	} // Dấu ngoặc nhọn đóng hàm
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
	    Integer currentUserId = currentUser.getUserId();

	    // 1. Lấy danh sách (có thể chứa null)
	    List<User> chatList = messageService.getConversationPartners(currentUserId);
	    List<GroupsUTE> groupList = groupService.getGroupsByUserId(currentUserId);
	    List<User> friends = friendService.getFriendList(currentUserId);

	    // 2. Logic thêm user mới vào chat (nếu chat lần đầu)
	    if (friendId != null) {
	        // (Kiểm tra xem user đã có trong danh sách CHƯA LỌC không)
	        boolean isAlreadyInList = chatList.stream()
	                                          .filter(u -> u != null) // Lọc null trước khi check
	                                          .anyMatch(user -> user.getUserId().equals(friendId));
	        if (!isAlreadyInList) {
	            User userToChat = userRepository.findById(friendId).orElse(null); 
	            if (userToChat != null) {
	                chatList.add(0, userToChat); // Thêm vào danh sách CHƯA LỌC
	            }
	        }
	    }
	    
	    // --- ⭐ LỌC NULL (GIỐNG HÀM TRÊN) ⭐ ---
	    List<User> cleanChatList = chatList.stream()
	                                       .filter(u -> u != null)
	                                       .collect(Collectors.toList());
	    List<GroupsUTE> cleanGroupList = groupList.stream()
	                                              .filter(g -> g != null)
	                                              .collect(Collectors.toList());
	    List<User> cleanFriendsList = friends.stream()
	                                         .filter(f -> f != null)
	                                         .collect(Collectors.toList());
	    // ----------------------------------------

	    // 4. Lấy Map tin nhắn cuối (Lặp qua danh sách SẠCH)
	    Map<Object, String> lastMessages = new HashMap<>(); 
	    for (User partner : cleanChatList) { 
	        String preview = messageService.getLatestMessagePreview(currentUserId, partner.getUserId());
	        lastMessages.put(partner.getUserId(), (preview != null ? preview : ""));
	    }
	    // (Bỏ qua preview nhóm theo yêu cầu của bạn)

	    // 5. Lấy số lượng tin nhắn chưa đọc
	    Map<Integer, Long> unreadUserCounts = 
	        messageService.getUnreadCountsPerSender(currentUserId);
	    Map<Integer, Long> unreadGroupCounts = new HashMap<>();

	    // 6. Đưa TẤT CẢ (danh sách SẠCH) ra Model
	    model.addAttribute("chatList", cleanChatList);
	    model.addAttribute("groupList", cleanGroupList); 
	    model.addAttribute("lastMessages", lastMessages); 
	    model.addAttribute("friends", cleanFriendsList);
	    model.addAttribute("unreadUserCounts", unreadUserCounts); 
	    model.addAttribute("unreadGroupCounts", unreadGroupCounts);
	    
	    // 7. Thêm ID để JS tự động mở
	    model.addAttribute("friendId", friendId); 
	    model.addAttribute("groupId", null);     

	    return "user/message";
	}
	@GetMapping("/message/group/{groupId}")
	public String openGroupMessagePage(@PathVariable Integer groupId, Model model, HttpSession session) {
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return "redirect:/login";
	    }
	    Integer currentUserId = currentUser.getUserId();

	    // 1. Lấy danh sách (có thể chứa null)
	    List<User> chatList = messageService.getConversationPartners(currentUserId);
	    List<GroupsUTE> groupList = groupService.getGroupsByUserId(currentUserId);
	    List<User> friends = friendService.getFriendList(currentUserId);

	    // --- ⭐ LỌC NULL (GIỐNG HÀM TRÊN) ⭐ ---
	    List<User> cleanChatList = chatList.stream()
	                                       .filter(u -> u != null)
	                                       .collect(Collectors.toList());
	    List<GroupsUTE> cleanGroupList = groupList.stream()
	                                              .filter(g -> g != null)
	                                              .collect(Collectors.toList());
	    List<User> cleanFriendsList = friends.stream()
	                                         .filter(f -> f != null)
	                                         .collect(Collectors.toList());
	    // ----------------------------------------

	    // 3. Lấy Map tin nhắn cuối (Lặp qua danh sách SẠCH)
	    Map<Object, String> lastMessages = new HashMap<>(); 
	    for (User partner : cleanChatList) { 
	        String preview = messageService.getLatestMessagePreview(currentUserId, partner.getUserId());
	        lastMessages.put(partner.getUserId(), (preview != null ? preview : ""));
	    }
	    // (Bỏ qua preview nhóm theo yêu cầu của bạn)

	    // 4. Lấy số lượng tin nhắn chưa đọc
	    Map<Integer, Long> unreadUserCounts = 
	        messageService.getUnreadCountsPerSender(currentUserId);
	    Map<Integer, Long> unreadGroupCounts = new HashMap<>();

	    // 5. Đưa TẤT CẢ (danh sách SẠCH) ra Model
	    model.addAttribute("chatList", cleanChatList);
	    model.addAttribute("groupList", cleanGroupList); 
	    model.addAttribute("lastMessages", lastMessages); 
	    model.addAttribute("friends", cleanFriendsList);
	    model.addAttribute("unreadUserCounts", unreadUserCounts); 
	    model.addAttribute("unreadGroupCounts", unreadGroupCounts);
	    
	    // 6. Thêm ID để JS tự động mở
	    model.addAttribute("friendId", null);     
	    model.addAttribute("groupId", groupId);   

	    return "user/message";
	}
	// Trong file: UserMessageController.java

	@PostMapping("/message/send")
	@ResponseBody
	public Map<String, Object> sendMessage(
	        @RequestParam("content") String content,
	        @RequestParam("receiverId") Integer receiverId,
	        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
	        HttpSession session
	) {
	    User currentUser = (User) session.getAttribute("user");
	    // Lấy ID người gửi
	    Integer senderId = currentUser.getUserId(); 

	    // 1. Lưu tin nhắn (như cũ)
	    Message saved = messageService.saveMessage(senderId, receiverId, content, attachments);

	    // 2. Tạo đối tượng JSON cơ bản (như cũ)
	    Map<String, Object> senderMap = Map.of(
	        "userId", saved.getSender().getUserId(),
	        "fullName", saved.getSender().getFullName(),
	        "avatarUrl", saved.getSender().getAvatarUrl()
	    );
	    Map<String, Object> receiverMap = Map.of("userId", saved.getReceiver().getUserId());
	    List<Map<String, Object>> attachmentList = saved.getAttachments() != null
	            ? saved.getAttachments().stream().map(att -> Map.<String, Object>of(
	                "fileUrl", att.getFileUrl(),
	                "fileType", att.getFileType()
	            )).toList() : List.of();

	    Map<String, Object> baseResponse = new HashMap<>();
	    baseResponse.put("messageId", saved.getMessageId());
	    baseResponse.put("content", saved.getContent());
	    baseResponse.put("createdAt", saved.getCreatedAt());
	    baseResponse.put("sender", senderMap);
	    baseResponse.put("receiver", receiverMap);
	    baseResponse.put("attachments", attachmentList);
	    baseResponse.put("groupId", null); // Tin nhắn 1-1

	    // --- ⭐ BẮT ĐẦU THAY ĐỔI ⭐ ---

	    // 3. Tính số lượng tin nhắn chưa đọc MÀ NGƯỜI GỬI (senderId) đã gửi cho NGƯỜI NHẬN (receiverId)
	    long unreadCountFromSender = messageService.getUnreadCountFromSender(senderId, receiverId);

	    // 4. Gửi tin nhắn GỐC đến người gửi (KHÔNG cần số lượng)
	    messagingTemplate.convertAndSend("/topic/messages/" + senderId, baseResponse);

	    // 5. Tạo một bản sao payload RIÊNG cho người nhận
	    Map<String, Object> responseForReceiver = new HashMap<>(baseResponse);
	    // Thêm số lượng chưa đọc TỪ NGƯỜI GỬI này vào payload
	    responseForReceiver.put("unreadFromSender", unreadCountFromSender); 

	    // 6. Gửi payload ĐÃ BỔ SUNG đến người nhận
	    messagingTemplate.convertAndSend("/topic/messages/" + receiverId, responseForReceiver);

	    // --- ⭐ KẾT THÚC THAY ĐỔI ⭐ ---

	    // 7. Trả về response GỐC cho AJAX của người gửi (để render ngay)
	    return baseResponse;
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
	@PostMapping("/message/mark-as-read")
	@ResponseBody // Trả về JSON (hoặc không cần gì cũng được)
	public Map<String, String> markChatAsRead(
	        @RequestBody Map<String, String> payload, 
	        HttpSession session) {
	    
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return Map.of("status", "error", "message", "Chưa đăng nhập");
	    }
	    
	    try {
	        // Lấy ID của người bạn (người gửi) mà ta vừa đọc tin nhắn
	        Integer friendId = Integer.parseInt(payload.get("friendId"));
	        Integer currentUserId = currentUser.getUserId();
	        
	        // Gọi Service để cập nhật CSDL
	        // (Chúng ta sẽ tạo hàm này ở bước tiếp theo)
	        messageService.markMessagesAsRead(friendId, currentUserId);
	        
	        return Map.of("status", "success");
	    } catch (Exception e) {
	        return Map.of("status", "error", "message", e.getMessage());
	    }
	}
	@GetMapping("/message/unread-count")
	@ResponseBody
	public Map<String, Long> getUnreadMessageCount(HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return Map.of("count", 0L);
	    }
	    
	    // (Chúng ta sẽ tạo hàm này ở bước 2b)
	    long count = messageService.getUnreadMessageCount(user.getUserId());
	    
	    return Map.of("count", count);
	}
}
