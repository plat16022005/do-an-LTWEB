package aloute.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Attachments")
public class Attachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttachmentID")
    private Integer attachmentId;

    // Quan hệ với Posts
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID")
    private Posts post;

    // Quan hệ với Messages
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MessageID")
    private Messages message;

    @Column(name = "FileURL", length = 255)
    private String fileUrl;

    @Column(name = "FileType", length = 50)
    private String fileType;

    @Column(name = "UploadedAt")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    // ===================== GETTERS & SETTERS =====================

    public Integer getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public Messages getMessage() {
        return message;
    }

    public void setMessage(Messages message) {
        this.message = message;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
