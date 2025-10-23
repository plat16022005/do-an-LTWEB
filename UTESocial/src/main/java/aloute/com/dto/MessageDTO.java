package aloute.com.dto;

public class MessageDTO {
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public boolean isSentByMe() {
		return sentByMe;
	}
	public void setSentByMe(boolean sentByMe) {
		this.sentByMe = sentByMe;
	}
	private Integer id;
    private String senderName;
    private String senderAvatar;
    private String content;
    private String time;
    private boolean sentByMe;

    // getters + setters
}

