package aloute.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PostModeration")
public class PostModeration 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer moderationId;

    @ManyToOne
    @JoinColumn(name = "PostID", nullable = false)
    private Posts post;

    @Column(name = "Status", nullable = false)
    private String status = "pending";

    @ManyToOne
    @JoinColumn(name = "ModeratorID")
    private User moderator;

    @Column(name = "ReviewedAt")
    private LocalDateTime reviewedAt;

    @Column(name = "Reason")
    private String reason;


    
    
	public Integer getModerationId() {
		return moderationId;
	}
	public void setModerationId(Integer moderationId) {
		this.moderationId = moderationId;
	}

	public Posts getPost() {
		return post;
	}
	public void setPost(Posts post) {
		this.post = post;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public User getModerator() {
		return moderator;
	}
	public void setModerator(User moderator) {
		this.moderator = moderator;
	}

	public LocalDateTime getReviewedAt() {
		return reviewedAt;
	}
	public void setReviewedAt(LocalDateTime reviewedAt) {
		this.reviewedAt = reviewedAt;
	}

	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

  
}
