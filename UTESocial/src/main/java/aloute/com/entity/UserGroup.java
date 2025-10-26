package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Đại diện cho bảng UserGroups (thành viên trong nhóm).
 * Bảng này liên kết Users và GroupsUTE.
 */
@Entity
@Table(name = "UserGroups")
public class UserGroup {

    /**
     * Khóa chính, ID tự tăng.
     * Ánh xạ: UserGroupID INT AUTO_INCREMENT PRIMARY KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserGroupID")
    private Integer userGroupId;

    /**
     * Mối quan hệ Nhiều-Một với User (thành viên).
     * Ánh xạ: UserID INT, FOREIGN KEY (UserID) REFERENCES Users(UserID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    private User user;

    /**
     * Mối quan hệ Nhiều-Một với GroupsUTE (nhóm).
     * Ánh xạ: GroupID INT, FOREIGN KEY (GroupID) REFERENCES GroupsUTE(GroupID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GroupID", referencedColumnName = "GroupID")
    private GroupsUTE group;

    /**
     * Ngày giờ tham gia nhóm, tự động gán.
     * Ánh xạ: JoinedAt DATETIME DEFAULT CURRENT_TIMESTAMP
     */
    @CreationTimestamp
    @Column(name = "JoinedAt", updatable = false)
    private LocalDateTime joinedAt;

    /**
     * Vai trò trong nhóm (ví dụ: 'admin', 'member').
     * Ánh xạ: RoleInGroup VARCHAR(20)
     */
    @Column(name = "RoleInGroup", length = 20)
    private String roleInGroup;

    // -----------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------

    public UserGroup() {
    }

    // -----------------------------------------------------------------
    // GETTERS AND SETTERS
    // -----------------------------------------------------------------

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GroupsUTE getGroup() {
        return group;
    }

    public void setGroup(GroupsUTE group) {
        this.group = group;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getRoleInGroup() {
        return roleInGroup;
    }

    public void setRoleInGroup(String roleInGroup) {
        this.roleInGroup = roleInGroup;
    }
}
