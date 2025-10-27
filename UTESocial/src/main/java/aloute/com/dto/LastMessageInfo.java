package aloute.com.dto; // Or your DTO package

import java.time.LocalDateTime;

public class LastMessageInfo {
    private String previewContent;
    private LocalDateTime createdAt;

    // Constructor
    public LastMessageInfo(String previewContent, LocalDateTime createdAt) {
        this.previewContent = (previewContent != null ? previewContent : ""); // Ensure not null
        this.createdAt = createdAt;
    }

    // Getters
    public String getPreviewContent() {
        return previewContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}