package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Đại diện cho bảng GroupsUTE trong cơ sở dữ liệu (Không dùng Lombok).
 */
@Entity
@Table(name = "GroupsUTE")
public class GroupsUTE {

    /**
     * Khóa chính, ID tự tăng của nhóm.
     * Ánh xạ: GroupID INT AUTO_INCREMENT PRIMARY KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GroupID")
    private Integer groupId;

    /**
     * Tên của nhóm, không được null.
     * Ánh xạ: NameGroup VARCHAR(100) NOT NULL
     */
    @Column(name = "NameGroup", nullable = false, length = 100)
    private String nameGroup;

    /**
     * Đường dẫn URL đến ảnh đại diện của nhóm.
     * Ánh xạ: Avatar VARCHAR(255)
     */
    @Column(name = "Avatar", length = 255)
    private String avatar;

    /**
     * Ngày giờ nhóm được tạo, tự động gán khi tạo mới.
     * Ánh xạ: CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
     */
    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Mối quan hệ Nhiều-Một: Nhiều nhóm có thể được tạo bởi một Người dùng.
     * Ánh xạ: CreatedBy INT, FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "UserID")
    private User createdBy;

    /**
     * Mối quan hệ Một-Nhiều: Một nhóm có nhiều bản ghi thành viên (UserGroups).
     */
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroup> members;

    /**
     * Mối quan hệ Một-Nhiều: Một nhóm có nhiều tin nhắn (GroupMessages).
     */
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMessage> messages;

    // -----------------------------------------------------------------
    // CONSTRUCTORS (Thay thế @NoArgsConstructor)
    // -----------------------------------------------------------------

    /**
     * Constructor mặc định, bắt buộc bởi JPA.
     */
    public GroupsUTE() {
    }

    // -----------------------------------------------------------------
    // GETTERS AND SETTERS (Thay thế @Getter và @Setter)
    // -----------------------------------------------------------------

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<UserGroup> getMembers() {
        return members;
    }

    public void setMembers(List<UserGroup> members) {
        this.members = members;
    }

    public List<GroupMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<GroupMessage> messages) {
        this.messages = messages;
    }
}
