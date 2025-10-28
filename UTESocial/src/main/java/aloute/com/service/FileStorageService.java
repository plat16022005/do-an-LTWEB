package aloute.com.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import aloute.com.entity.Attachments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    // 📁 Thư mục gốc lưu file trên server (có thể tùy chỉnh)
    private final Path root = Paths.get("uploads");
    private final aloute.com.repository.AttachmentRepository attachmentRepository;

    public FileStorageService(aloute.com.repository.AttachmentRepository attachmentRepository) 
    {
    	this.attachmentRepository = attachmentRepository;
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload!", e);
        }
    }


    public Attachments getAttachmentInfo(Integer attachmentId) 
    {
        return attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tệp đính kèm với ID: " + attachmentId));
    }

    /**
     * 📥 Lưu file và trả về đường dẫn URL để sử dụng trong front-end.
     */
    public String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = this.root.resolve(fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // ⚠️ Quan trọng: Tùy vào cấu hình server mà bạn trả URL phù hợp
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("❌ Lỗi khi lưu file: " + file.getOriginalFilename(), e);
        }
    }
    
    public Resource loadFileAsResource(String filename) 
    {
        try {
            Path filePath = this.root.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Ném lỗi nếu file không tồn tại hoặc không đọc được
                throw new RuntimeException("Không thể đọc file: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Lỗi đường dẫn file: " + filename, ex);
        }
    }
}
