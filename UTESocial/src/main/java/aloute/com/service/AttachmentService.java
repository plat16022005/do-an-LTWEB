package aloute.com.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import aloute.com.entity.Attachments;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.AttachmentRepository;
import aloute.com.repository.common.PostsRepository;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PostsRepository postRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, PostsRepository postRepository) {
        this.attachmentRepository = attachmentRepository;
        this.postRepository = postRepository;
    }

    /**
     * 🗑️ Xóa nhiều file theo ID
     */
    public void deleteByIds(List<Integer> ids) {
        attachmentRepository.deleteAllById(ids);
    }

    /**
     * 📎 Lưu file đính kèm mới
     */
    public void saveNewAttachments(Integer postId, List<MultipartFile> newFiles, HttpSession session) {
        Posts post = postRepository.findById(postId).orElseThrow();

        for (MultipartFile file : newFiles) {
            if (!file.isEmpty()) {
                String fileUrl = saveFileAndGetUrl(file, session); // ✅ truyền đủ 2 tham số
                String fileType = file.getContentType();

                Attachments att = new Attachments();
                att.setPost(post);
                att.setFileUrl(fileUrl);
                att.setFileType(fileType);
                attachmentRepository.save(att);
            }
        }
    }



    // ⚠️ Ví dụ đơn giản (bạn có thể thay bằng logic upload thực tế)
    private String saveFileAndGetUrl(MultipartFile file, HttpSession session) {
    	User user = (User) session.getAttribute("user");
        try {
            // 🏷️ Lấy loại file (VD: image/jpeg, video/mp4, application/pdf,...)
            String contentType = file.getContentType();
            String subFolder;

            if (contentType != null) {
                if (contentType.startsWith("image")) {
                    subFolder = "posts";
                } else if (contentType.startsWith("video")) {
                    subFolder = "videos";
                } else {
                    subFolder = "other";
                }
            } else {
                subFolder = "other";
            }

            String safeUserName = user.getNameUser();

            String baseDir = "uploads";
            String uploadDir = baseDir + "/" + subFolder + "/" + safeUserName;
            Path uploadPath = Paths.get(uploadDir);

            // ✅ Tạo cả thư mục nếu chưa có
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }


            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // ✅ Trả về URL trùng với đường dẫn thực tế
            return "/" + baseDir + "/" + subFolder + "/" + safeUserName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }

}
