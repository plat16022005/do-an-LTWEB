package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Đại diện cho bảng GroupMessages (tin nhắn trong nhóm).
 */
@Entity
@Table(name = "GroupMessages")
public class GroupMessage {

    /**
     * Khóa chính, ID tự tăng.
     * Ánh xạ: GroupMessageID INT AUTO_INCREMENT PRIMARY KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GroupMessageID")
    private Integer groupMessageId;

    /**
     * Mối quan hệ Nhiều-Một: Nhóm mà tin nhắn được gửi đến.
     * Ánh xạ: GroupID INT NOT NULL, FOREIGN KEY (GroupID) REFERENCES GroupsUTE(GroupID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GroupID", referencedColumnName = "GroupID", nullable = false)
    private GroupsUTE group;

    /**
     * Mối quan hệ Nhiều-Một: Người gửi tin nhắn.
     * Ánh xạ: SenderID INT NOT NULL, FOREIGN KEY (SenderID) REFERENCES Users(UserID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SenderID", referencedColumnName = "UserID", nullable = false)
    private User sender;

    /**
     * Ngày giờ gửi tin nhắn, tự động gán.
     * Ánh xạ: CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
     */
    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Nội dung tin nhắn.
     * Ánh xạ: Content VARCHAR(1000)
     */
    @Column(name = "Content", length = 1000)
    private String content;

    /**
     * Trạng thái đã đọc (chưa dùng tới trong SQL nhưng có định nghĩa).
     * Ánh xạ: IsRead BOOLEAN DEFAULT FALSE
     */
    @Column(name = "IsRead")
    private Boolean isRead = false;

    /**
     * Mối quan hệ Một-Nhiều: Một tin nhắn có thể có nhiều tệp đính kèm.
     */
    @OneToMany(mappedBy = "groupMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupAttachment> attachments;
    
    // -----------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------

    public GroupMessage() {
    }

    // -----------------------------------------------------------------
    // GETTERS AND SETTERS
    // -----------------------------------------------------------------

    public Integer getGroupMessageId() {
        return groupMessageId;
    }

    public void setGroupMessageId(Integer groupMessageId) {
        this.groupMessageId = groupMessageId;
    }

    public GroupsUTE getGroup() {
        return group;
    }

    public void setGroup(GroupsUTE group) {
        this.group = group;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public List<GroupAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<GroupAttachment> attachments) {
        this.attachments = attachments;
    }
}
