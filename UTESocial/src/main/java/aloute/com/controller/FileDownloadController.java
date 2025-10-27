package aloute.com.controller;

import aloute.com.entity.Attachments;
import aloute.com.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger; // Thêm logger
import org.slf4j.LoggerFactory; // Thêm logger

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException; // Bắt lỗi đường dẫn

@Controller
public class FileDownloadController {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadController.class); // Khai báo logger

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/download/attachment/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Integer attachmentId,
            HttpServletRequest request
    ) {
        logger.info("Yêu cầu tải attachment ID: {}", attachmentId); // Log khi có yêu cầu

        // 1. Lấy thông tin attachment
        Attachments attachment;
        try {
            attachment = fileStorageService.getAttachmentInfo(attachmentId);
        } catch (ResponseStatusException e) {
            logger.warn("Không tìm thấy attachment ID {} để tải: {}", attachmentId, e.getMessage());
            return ResponseEntity.notFound().build(); // Trả về 404 Not Found
        }

        String relativeFileUrl = attachment.getFileUrl(); // Ví dụ: "/uploads/uuid_tenfile.pdf"
        if (relativeFileUrl == null || relativeFileUrl.isBlank()) {
            logger.error("Attachment ID {} không có đường dẫn file (fileUrl is blank).", attachmentId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Tách tên file thực tế khỏi prefix (ví dụ: "/uploads/")
        String storedFilename;
        if (relativeFileUrl.startsWith("/uploads/")) {
            storedFilename = relativeFileUrl.substring("/uploads/".length()); // Lấy phần sau "/uploads/"
        } else {
            storedFilename = relativeFileUrl; // Nếu không có prefix thì dùng luôn
        }
        logger.debug("Tên file cần tải từ service: '{}'", storedFilename); // Tên file thực tế: uuid_tenfile.pdf

        // 3. Tải file dưới dạng Resource
        Resource resource;
        try {
            resource = fileStorageService.loadFileAsResource(storedFilename);
        } catch (ResponseStatusException e) {
            logger.error("Lỗi khi tải resource cho file '{}' (Attachment ID {}): {}", storedFilename, attachmentId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).build(); // Trả về lỗi tương ứng (404, 400, 500)
        } catch (Exception e) { // Bắt các lỗi khác (ví dụ SecurityException)
            logger.error("Lỗi không xác định khi tải resource cho file '{}' (Attachment ID {}): {}", storedFilename, attachmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // 4. Xác định Content Type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            logger.debug("MIME type tự động xác định cho '{}': {}", storedFilename, contentType);
        } catch (Exception e) {
            logger.warn("Không thể xác định MIME type tự động cho '{}'. Sẽ dùng fileType từ DB hoặc mặc định.", storedFilename);
        }

        // Fallback nếu không xác định được tự động
        if (contentType == null) {
            String fileTypeFromDb = attachment.getFileType();
            logger.debug("Sử dụng fileType từ DB: '{}'", fileTypeFromDb);
            if ("pdf".equalsIgnoreCase(fileTypeFromDb)) {
                contentType = MediaType.APPLICATION_PDF_VALUE; // "application/pdf"
            } else if ("docx".equalsIgnoreCase(fileTypeFromDb)) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if ("doc".equalsIgnoreCase(fileTypeFromDb)) {
                contentType = "application/msword";
            } else if ("xlsx".equalsIgnoreCase(fileTypeFromDb)) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if ("xls".equalsIgnoreCase(fileTypeFromDb)) {
                contentType = "application/vnd.ms-excel";
            } // Thêm các loại file khác nếu cần
            else {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Loại mặc định
                logger.debug("Không khớp fileType DB, dùng mặc định: {}", contentType);
            }
        }

        // 5. Xác định tên file gốc để hiển thị cho người dùng
        // Cần có 1 trường riêng trong Attachments để lưu tên gốc, ví dụ: originalFilename
        // Tạm thời dùng tên file đã lưu, bỏ phần UUID đi nếu có
        String originalFilename = storedFilename;
        if (storedFilename.contains("_")) {
            try {
                originalFilename = storedFilename.substring(storedFilename.indexOf('_') + 1);
             } catch (Exception e) { /* Bỏ qua nếu có lỗi */ }
        }
        logger.debug("Tên file gốc dự kiến: '{}'", originalFilename);


        // 6. Tạo ResponseEntity
        try {
            String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.toString()).replace("+", "%20");
            String contentDisposition = "attachment; filename=\"" + originalFilename + "\"; filename*=UTF-8''" + encodedFilename;
            logger.debug("Content-Disposition header: {}", contentDisposition);

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                // .contentLength(resource.contentLength()) // Bỏ comment nếu muốn thêm Content-Length
                .body(resource);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo ResponseEntity cho file '{}': {}", originalFilename, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi chuẩn bị file để tải về", e);
        }
    }
}