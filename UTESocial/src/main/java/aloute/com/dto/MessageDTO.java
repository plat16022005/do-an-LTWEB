package aloute.com.dto;

import java.util.List;

public class MessageDTO {
    public Integer getMessageId() {
		return messageId;
	}
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderAvatar() {
		return senderAvatar;
	}
	public void setSenderAvatar(String senderAvatar) {
		this.senderAvatar = senderAvatar;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public List<AttachmentDTO> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<AttachmentDTO> attachments) {
		this.attachments = attachments;
	}
	private Integer messageId;
    private String content;
    private String createdAt;

    private Integer senderId;
    private String senderName;
    private String senderAvatar;

    private Integer receiverId;
    private List<AttachmentDTO> attachments;

    // getters / setters
}


