package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Đại diện cho bảng GroupAttachments (tệp đính kèm trong tin nhắn nhóm).
 */
@Entity
@Table(name = "GroupAttachments")
public class GroupAttachment {

    /**
     * Khóa chính, ID tự tăng.
     * Ánh xạ: AttachmentID INT AUTO_INCREMENT PRIMARY KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttachmentID")
    private Integer attachmentId;

    /**
     * Mối quan hệ Nhiều-Một: Tin nhắn mà tệp này được đính kèm.
     * Ánh xạ: GroupMessageID INT, FOREIGN KEY (GroupMessageID) REFERENCES GroupMessages(GroupMessageID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GroupMessageID", referencedColumnName = "GroupMessageID")
    private GroupMessage groupMessage;

    /**
     * Đường dẫn URL đến tệp.
     * Ánh xạ: FileURL VARCHAR(255)
     */
    @Column(name = "FileURL", length = 255)
    private String fileURL;

    /**
     * Loại tệp (ví dụ: 'image/jpeg', 'video/mp4').
     * Ánh xạ: FileType VARCHAR(50)
     */
    @Column(name = "FileType", length = 50)
    private String fileType;

    /**
     * Ngày giờ tải lên, tự động gán.
     * Ánh xạ: UploadedAt DATETIME DEFAULT CURRENT_TIMESTAMP
     */
    @CreationTimestamp
    @Column(name = "UploadedAt", updatable = false)
    private LocalDateTime uploadedAt;

    // -----------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------
    
    public GroupAttachment() {
    }

    // -----------------------------------------------------------------
    // GETTERS AND SETTERS
    // -----------------------------------------------------------------

    public Integer getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }

    public GroupMessage getGroupMessage() {
        return groupMessage;
    }

    public void setGroupMessage(GroupMessage groupMessage) {
        this.groupMessage = groupMessage;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
