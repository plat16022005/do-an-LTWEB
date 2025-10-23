package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho mối quan hệ chặn giữa 2 người dùng
 * - blocker: Người thực hiện chặn
 * - blocked: Người bị chặn
 */
@Entity
@Table(name = "BlockedUser", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_userId", "blocked_userId"})
)
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blockId")
    private Integer blockId;

    /**
     * Người chặn (blocker)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blocker_userId", referencedColumnName = "UserID", nullable = false)
    private User blocker;

    /**
     * Người bị chặn (blocked)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blocked_userId", referencedColumnName = "UserID", nullable = false)
    private User blocked;

    /**
     * Thời điểm chặn
     */
    @Column(name = "blockedAt", nullable = false)
    private LocalDateTime blockedAt;

    // ===== Constructors =====
    
    public BlockedUser() {
        this.blockedAt = LocalDateTime.now();
    }

    public BlockedUser(User blocker, User blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
        this.blockedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public User getBlocker() {
        return blocker;
    }

    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }

    public User getBlocked() {
        return blocked;
    }

    public void setBlocked(User blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    @Override
    public String toString() {
        return "BlockedUser{" +
                "blockId=" + blockId +
                ", blocker=" + (blocker != null ? blocker.getUserId() : null) +
                ", blocked=" + (blocked != null ? blocked.getUserId() : null) +
                ", blockedAt=" + blockedAt +
                '}';
    }
}
