package aloute.com.service;

import aloute.com.entity.GroupAttachment;
import aloute.com.entity.GroupMessage;
import aloute.com.entity.GroupsUTE;
import aloute.com.entity.User;
import aloute.com.repository.GroupAttachmentRepository;
import aloute.com.repository.GroupMessageRepository;
import aloute.com.repository.GroupsUTERepository; // ⬅️ Cần Repo này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupMessageService {

    private final GroupMessageRepository groupMessageRepository;
    private final GroupsUTERepository groupsUTERepository;
    private final GroupAttachmentRepository groupAttachmentRepository;

    @Autowired
    public GroupMessageService(GroupMessageRepository groupMessageRepository,
                               GroupsUTERepository groupsUTERepository,
                               GroupAttachmentRepository groupAttachmentRepository) {
        this.groupMessageRepository = groupMessageRepository;
        this.groupsUTERepository = groupsUTERepository;
        this.groupAttachmentRepository = groupAttachmentRepository;
    }

    /**
     * Lấy tất cả tin nhắn của một nhóm (đã sắp xếp)
     */
    @Transactional(readOnly = true)
    public List<GroupMessage> getMessagesByGroupId(Integer groupId) {
        // (Sử dụng hàm bạn đã tạo trong GroupMessageRepository)
        return groupMessageRepository.findMessagesByGroupIdWithDetails(groupId);
    }

    /**
     * Lưu tin nhắn nhóm mới và xử lý file đính kèm.
     * ⭐ ĐÂY LÀ HÀM ĐÃ SỬA ⭐
     */
    @Transactional
    public GroupMessage saveGroupMessage(User sender, Integer groupId, String content, List<MultipartFile> files) {
        
        // 1. Tìm GroupsUTE entity
        GroupsUTE group = groupsUTERepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm với ID: " + groupId));

        // 2. Tạo đối tượng GroupMessage
        GroupMessage message = new GroupMessage();
        message.setSender(sender);
        message.setGroup(group);
        message.setContent(content);

        // 3. Xử lý file đính kèm (SAO CHÉP TỪ MESSAGESERVICE CỦA BẠN)
        List<GroupAttachment> attachmentList = new ArrayList<>();
        
        if (files != null && !files.isEmpty()) {
            
            // Định nghĩa đường dẫn (khác với 1-1 để dễ quản lý)
            final String WEB_PATH = "/uploads/group-messages/";
            final String PHYSICAL_PATH_ROOT = System.getProperty("user.dir") + "/uploads/group-messages/";

            File uploadDir = new File(PHYSICAL_PATH_ROOT);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Tạo thư mục
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // Tạo tên file an toàn
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null) originalFileName = "file";
                
                // GỌI HÀM HELPER (sanitizeFileName)
                String fileName = System.currentTimeMillis() + "_" + sanitizeFileName(originalFileName); 
                
                // File đích (vật lý)
                File dest = new File(PHYSICAL_PATH_ROOT + fileName);

                try {
                    file.transferTo(dest); // Lưu file vật lý
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Không thể lưu file", e); // Báo lỗi để rollback
                }

                // Tạo đối tượng GroupAttachment
                GroupAttachment attachment = new GroupAttachment();
                attachment.setFileURL(WEB_PATH + fileName); // Lưu đường dẫn web
                attachment.setFileType(file.getContentType());
                
                // ⭐ QUAN TRỌNG: Liên kết 2 chiều
                attachment.setGroupMessage(message); 
                
                attachmentList.add(attachment);
            }
        }

        // 4. Set danh sách attachments vào tin nhắn
        message.setAttachments(attachmentList);

        // 5. LƯU: 
        // Nhờ `CascadeType.ALL`, attachments sẽ được lưu cùng lúc
        return groupMessageRepository.save(message);
    }

    /**
     * ⭐ HÀM NÀY ĐƯỢC COPY TỪ MESSAGESERVICE SANG
     */
    private String sanitizeFileName(String filename) {
        // Thay thế mọi thứ KHÔNG phải là chữ, số, dấu gạch dưới, gạch ngang, dấu chấm
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}