package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FriendID")
    private Integer friendId;

    // Quan hệ ManyToOne tới Users (UserID1)
    @ManyToOne
    @JoinColumn(name = "UserID1", referencedColumnName = "UserID")
    private User user1;

    // Quan hệ ManyToOne tới Users (UserID2)
    @ManyToOne
    @JoinColumn(name = "UserID2", referencedColumnName = "UserID")
    private User user2;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "RequestDate")
    private LocalDateTime requestDate;

    @Column(name = "AcceptDate")
    private LocalDateTime acceptDate;

    // ===== Getter & Setter =====
    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(LocalDateTime acceptDate) {
        this.acceptDate = acceptDate;
    }
}
