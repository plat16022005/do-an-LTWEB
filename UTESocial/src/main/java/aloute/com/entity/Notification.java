package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationID")
    private Integer notificationId;

    // 🔗 Khóa ngoại liên kết với bảng Users
    @ManyToOne(fetch = FetchType.LAZY)   // EAGER nếu bạn muốn load sẵn thông tin user
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "Type", length = 50, nullable = false)
    private String type;

    @Column(name = "RelatedID")
    private Integer relatedId;

    @Column(name = "Content", length = 255)
    private String content;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "IsRead")
    private Boolean isRead = false;
    
    public String getActorAvatar() {
		return actorAvatar;
	}

	public void setActorAvatar(String actorAvatar) {
		this.actorAvatar = actorAvatar;
	}

	@Column(name = "ActorAvatar", length = 255)
    private String actorAvatar;

    // =======================
    // Getter & Setter
    // =======================

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
