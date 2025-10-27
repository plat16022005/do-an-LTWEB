package aloute.com.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import aloute.com.dto.LastMessageInfo;
import aloute.com.entity.Attachments;
import aloute.com.entity.Message;
import aloute.com.entity.User;
import aloute.com.repository.AttachmentRepository;
import aloute.com.repository.MessageRepository;
import aloute.com.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentsRepository;

    public MessageService(MessageRepository messageRepository,
                        UserRepository userRepository,
                        AttachmentRepository attachmentsRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.attachmentsRepository = attachmentsRepository;
    }

    @Transactional(readOnly = true)
    public LastMessageInfo getLatestMessageInfo(Integer userId1, Integer userId2) {
        // Assuming findLatestMessageBetween returns the latest Message object
        // You might need Pageable.ofSize(1) here
        List<Message> latestMessages = messageRepository.findLatestMessageBetween(
                userId1, userId2, org.springframework.data.domain.PageRequest.of(0, 1)
        );

        if (latestMessages.isEmpty()) {
            return new LastMessageInfo("", null); // No messages yet
        }

        Message latestMessage = latestMessages.get(0);
        String preview = generatePreview(latestMessage); // Use a helper to generate preview text

        return new LastMessageInfo(preview, latestMessage.getCreatedAt());
    }
    private String generatePreview(Message msg) {
        if (msg == null) return "";

        String content = msg.getContent();
        String prefix = (msg.getSender() != null && msg.getSender().getUserId().equals(USER_ID_FROM_SOMEWHERE)) // Need current user ID logic here, maybe pass it in?
                        ? "Bạn: " : ""; // Add "Bạn: " prefix if sent by current user

        if (content != null && !content.isEmpty()) {
            return prefix + content;
        } else if (msg.getAttachments() != null && !msg.getAttachments().isEmpty()) {
            // Determine preview based on attachment type (similar to JS)
            Attachments firstAtt = msg.getAttachments().iterator().next(); // Get first attachment
            String fileType = firstAtt.getFileType() != null ? firstAtt.getFileType().toLowerCase() : "";
            if (fileType.startsWith("image")) return prefix + "🖼️ [Hình ảnh]";
            if (fileType.startsWith("video")) return prefix + "📹 [Video]";
            return prefix + "📎 [Tệp đính kèm]";
        }
        return prefix + "..."; // Fallback
    }

    // !! IMPORTANT !! You need a way to get the current USER_ID inside generatePreview
    // or adjust the logic. Maybe getLatestMessageInfo should return the sender ID too.
    // Let's simplify for now and remove the "Bạn: " prefix in the service.

    private String generatePreviewSimple(Message msg) {
         if (msg == null) return "";
         String content = msg.getContent();
         if (content != null && !content.isEmpty()) {
             return content;
         } else if (msg.getAttachments() != null && !msg.getAttachments().isEmpty()) {
            Attachments firstAtt = msg.getAttachments().iterator().next();
            String fileType = firstAtt.getFileType() != null ? firstAtt.getFileType().toLowerCase() : "";
            if (fileType.startsWith("image")) return "🖼️ [Hình ảnh]";
            if (fileType.startsWith("video")) return "📹 [Video]";
            return "📎 [Tệp đính kèm]";
         }
         return "...";
    }

    // Modify getLatestMessageInfo to use the simple preview
    @Transactional(readOnly = true)
    public LastMessageInfo getLatestMessageInfo(Integer userId1, Integer userId2) {
        // ... find latest message ...
        if (latestMessages.isEmpty()) {
            return new LastMessageInfo("", null);
        }
        Message latestMessage = latestMessages.get(0);
        String preview = generatePreviewSimple(latestMessage); // Use simple preview
        // Determine if current user sent it (needed for "Bạn: " prefix in Thymeleaf later)
        boolean sentByMe = latestMessage.getSender() != null && latestMessage.getSender().getUserId().equals(userId1); // Assuming userId1 is current user

        // Pass 'sentByMe' info along, maybe modify LastMessageInfo DTO?
        // Let's keep it simple and handle "Bạn: " in Thymeleaf for now.
        return new LastMessageInfo(preview, latestMessage.getCreatedAt());
    }
    @Transactional(readOnly = true)
    public List<Message> getAllMessagesBetween(Integer userId1, Integer userId2) {
        return messageRepository.findAllMessagesWithAttachments(userId1, userId2);
    }

    public Message saveMessage(Integer senderId, Integer receiverId, String content, List<MultipartFile> files) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message = messageRepository.save(message); // Lưu trước để lấy ID

        // 📎 Nếu có file đính kèm
        if (files != null && !files.isEmpty()) {
            List<Attachments> savedAttachments = new ArrayList<>();
            
            // ⭐ SỬA LẠI: Định nghĩa 2 đường dẫn để KHỚP với WebConfig
            
            // 1. Đường dẫn WEB (URL trình duyệt gọi)
            // Phải là /uploads/messages/ để khớp với pattern "/uploads/**" trong WebConfig
            final String WEB_PATH = "/uploads/messages/";

            // 2. Đường dẫn VẬT LÝ (nơi lưu file)
            // Phải là thư mục con bên trong thư mục WebConfig đã khai báo
            final String PHYSICAL_PATH_ROOT = System.getProperty("user.dir") + "/uploads/messages/";
            
            // Đảm bảo thư mục vật lý tồn tại
            File uploadDir = new File(PHYSICAL_PATH_ROOT);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Tạo thư mục [ProjectFolder]/uploads/messages/
            }

            for (MultipartFile file : files) {
                // Đảm bảo tên file không chứa các ký tự như ".." (security)
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null) originalFileName = "file";
                String fileName = System.currentTimeMillis() + "_" + Chỉ_giữ_ký_tự_an_toàn(originalFileName);
                
                // Tạo file đích bằng đường dẫn VẬT LÝ
                File dest = new File(PHYSICAL_PATH_ROOT + fileName);

                try {
                    file.transferTo(dest); // Lưu file vào [ProjectFolder]/uploads/messages/
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Attachments attachment = new Attachments();
                attachment.setMessage(message);
                // Lưu đường dẫn WEB vào CSDL
                attachment.setFileUrl(WEB_PATH + fileName); 
                attachment.setFileType(file.getContentType());

                savedAttachments.add(attachmentsRepository.save(attachment));
            }

            message.setAttachments(savedAttachments);
        }

        return messageRepository.save(message);
    }
    private String Chỉ_giữ_ký_tự_an_toàn(String filename) {
        // Thay thế mọi thứ KHÔNG phải là chữ, số, dấu gạch dưới, dấu gạch ngang, dấu chấm
        // để tránh lỗi "Path Traversal"
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    public List<User> getConversationPartners(Integer currentUserId) {
        // 1. Lấy danh sách ID đã được sắp xếp từ DB
        List<Integer> sortedPartnerIds = messageRepository.findDistinctConversationPartnerIdsSortedByRecent(currentUserId);

        if (sortedPartnerIds == null || sortedPartnerIds.isEmpty()) {
            return new ArrayList<>(); // Trả về danh sách rỗng
        }

        // 2. Lấy danh sách User từ các ID (lưu ý: hàm này trả về không theo thứ tự)
        List<User> partners = userRepository.findAllById(sortedPartnerIds);

        // 3. Sắp xếp lại danh sách User theo đúng thứ tự của sortedPartnerIds
        Map<Integer, User> userMap = partners.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));
        
        List<User> sortedPartners = sortedPartnerIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull) // Lọc ra nếu có ID nào đó không tìm thấy User
                .collect(Collectors.toList());

        return sortedPartners;
    }
    @Transactional
    public void markMessagesAsRead(Integer senderId, Integer receiverId) {
        // Chúng ta sẽ tạo hàm này trong Repository
        messageRepository.updateReadStatus(senderId, receiverId);
    }
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Integer currentUserId) {
        // (Chúng ta sẽ tạo hàm này ở bước 2c)
        return messageRepository.countByReceiver_UserIdAndIsReadFalse(currentUserId);
    }
    @Transactional(readOnly = true)
    public Map<Integer, Long> getUnreadCountsPerSender(Integer currentUserId) {
        List<MessageRepository.UnreadCountPerSender> counts = 
            messageRepository.getUnreadCountsPerSender(currentUserId);
        
        // Chuyển List<DTO> thành Map<Integer, Long>
        return counts.stream().collect(
            Collectors.toMap(
                MessageRepository.UnreadCountPerSender::getSenderId,
                MessageRepository.UnreadCountPerSender::getUnreadCount
            )
        );
    }
    @Transactional(readOnly = true) // Quan trọng: chỉ đọc
    public long getUnreadCountFromSender(Integer senderId, Integer receiverId) {
        return messageRepository.countBySender_UserIdAndReceiver_UserIdAndIsReadFalse(senderId, receiverId);
    }
}
