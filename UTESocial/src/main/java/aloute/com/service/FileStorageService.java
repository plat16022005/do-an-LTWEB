package aloute.com.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    // 📁 Thư mục gốc lưu file trên server (có thể tùy chỉnh)
    private final Path root = Paths.get("uploads");

    public FileStorageService() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload!", e);
        }
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
}
