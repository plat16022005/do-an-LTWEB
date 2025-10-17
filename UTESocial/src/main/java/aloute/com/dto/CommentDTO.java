package aloute.com.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
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
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public List<CommentDTO> getReplies() {
		return replies;
	}
	public void setReplies(List<CommentDTO> replies) {
		this.replies = replies;
	}
	private Integer id;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private List<CommentDTO> replies;

    // Getter + Setter
}
