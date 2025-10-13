package aloute.com.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "Posts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "moderations"}) // tránh lỗi JSON vòng lặp
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID")
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    @JsonIgnoreProperties({"posts"}) // tránh vòng lặp User <-> Posts
    private User user;

    @Lob
    @Column(name = "Content")
    private String content;

    @Column(name = "Status", length = 20)
    private String status = "approved"; // mặc định

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "LikesCount")
    private int likesCount = 0;

    @Column(name = "ShareCount")
    private int shareCount = 0;

    @Column(name = "CommentsCount")
    private int commentsCount = 0;

    @Column(name = "Visibility", length = 20)
    private String visibility = "public";

    @Column(name = "IsDeleted")
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("post")
    private List<PostModeration> moderations;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Attachments> attachments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ===================== GETTERS & SETTERS =====================

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<PostModeration> getModerations() {
        return moderations;
    }

    public void setModerations(List<PostModeration> moderations) {
        this.moderations = moderations;
    }
    
 // Getter & Setter
    public List<Attachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachments> attachments) {
        this.attachments = attachments;
    }
}
