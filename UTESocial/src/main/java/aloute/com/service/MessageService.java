package aloute.com.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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

    public String getLatestMessagePreview(Integer userId1, Integer userId2) {
        List<Message> result = messageRepository.findLatestMessageBetween(
                userId1, userId2, PageRequest.of(0, 1)
        );

        if (result.isEmpty()) {
            return "Nhắn tin với bạn này...";
        }

        Message latest = result.get(0);
        String content = latest.getContent();
        if (content == null || content.trim().isEmpty()) {
            content = "[Tệp đính kèm]";
        }

        if (content.length() > 30) {
            content = content.substring(0, 30) + "...";
        }

        if (latest.getSender().getUserId().equals(userId1)) {
            return "Bạn: " + content;
        } else {
            return latest.getSender().getFullName() + ": " + content;
        }
    }
    @Transactional(readOnly = true)
    public List<Message> getAllMessagesBetween(Integer userId1, Integer userId2) {
        return messageRepository.findAllMessagesWithAttachments(userId1, userId2);
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unknown_file";
        }
        // Loại bỏ đường dẫn (security) và các ký tự không an toàn
        // Giữ lại chữ, số, dấu gạch dưới, dấu gạch ngang, dấu chấm
        return StringUtils.cleanPath(fileName).replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }


    // Hàm saveMessage của bạn với logic kiểm tra FileType được thêm vào
    @Transactional // <<< THÊM ANNOTATION NÀY
    public Message saveMessage(Integer senderId, Integer receiverId, String content, List<MultipartFile> files) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        // Lưu message trước để có ID, Hibernate sẽ tự quản lý việc update attachments sau
        message = messageRepository.save(message);

        // 📎 Nếu có file đính kèm
        if (files != null && !files.isEmpty()) {
            List<Attachments> savedAttachments = new ArrayList<>();

            // Đường dẫn lưu file vật lý
            final String PHYSICAL_PATH_ROOT = System.getProperty("user.dir") + "/uploads/messages/";
            // Đường dẫn web (URL)
            final String WEB_PATH = "/uploads/messages/";

            File uploadDir = new File(PHYSICAL_PATH_ROOT);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Tạo thư mục nếu chưa có
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue; // Bỏ qua file rỗng

                String originalFileName = file.getOriginalFilename();
                // Làm sạch tên file và tạo tên duy nhất
                String fileName = System.currentTimeMillis() + "_" + sanitizeFileName(originalFileName);

                File dest = new File(PHYSICAL_PATH_ROOT + fileName);

                try {
                    file.transferTo(dest); // Lưu file vật lý
                } catch (IOException e) {
                    System.err.println("Lỗi khi lưu file: " + fileName + " - " + e.getMessage());
                    // Có thể bỏ qua file này hoặc throw exception tùy logic của bạn
                    continue; // Bỏ qua file lỗi này và xử lý file tiếp theo
                }

                String originalFileType = file.getContentType(); // Lấy MIME type gốc

                // ⭐ KIỂM TRA VÀ GÁN LẠI FileType ⭐
                String fileTypeToSave;
                if (originalFileType != null) {
                    if (originalFileType.startsWith("image/")) {
                        fileTypeToSave = originalFileType;
                    } else if (originalFileType.startsWith("video/")) {
                        fileTypeToSave = originalFileType;
                    } else if (originalFileType.equals("application/pdf")) {
                        fileTypeToSave = "application/pdf";
                    } else {
                        // Các loại khác (docx, xlsx, zip,...) -> "other"
                        fileTypeToSave = "other";
                    }
                } else {
                    fileTypeToSave = "other"; // Không xác định được kiểu
                }
                // ⭐ KẾT THÚC THAY ĐỔI ⭐

                Attachments attachment = new Attachments();
                attachment.setMessage(message); // Liên kết với message đã lưu
                attachment.setFileUrl(WEB_PATH + fileName); // Lưu URL web
                attachment.setFileType(fileTypeToSave); // <<< Gán giá trị đã kiểm tra

                savedAttachments.add(attachmentsRepository.save(attachment)); // Lưu attachment
            }

            // Cập nhật lại danh sách attachments cho message (quan trọng)
            // Hibernate sẽ tự động quản lý mối quan hệ @OneToMany
             if (!savedAttachments.isEmpty()) {
                 message.setAttachments(savedAttachments);
                 // Không cần gọi messageRepository.save(message) lại lần nữa
                 // vì message đang trong trạng thái managed của @Transactional
             }
        }

        // Trả về message đã được cập nhật (nếu có attachments)
        // hoặc message gốc (nếu không có attachments)
        return message;
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