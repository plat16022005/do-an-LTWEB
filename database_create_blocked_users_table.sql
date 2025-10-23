-- Tạo bảng BlockedUser để lưu trữ mối quan hệ chặn giữa người dùng
-- Chạy script này trong MySQL để tạo bảng

CREATE TABLE IF NOT EXISTS BlockedUser (
    blockId BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_userId INT NOT NULL,
    blocked_userId INT NOT NULL,
    blockedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_blocker FOREIGN KEY (blocker_userId) REFERENCES Users(UserID) ON DELETE CASCADE,
    CONSTRAINT fk_blocked FOREIGN KEY (blocked_userId) REFERENCES Users(UserID) ON DELETE CASCADE,
    
    -- Unique constraint: mỗi cặp blocker-blocked chỉ xuất hiện 1 lần
    CONSTRAINT unique_block_pair UNIQUE (blocker_userId, blocked_userId),
    
    -- Check constraint: không cho phép tự chặn mình
    CONSTRAINT check_not_self_block CHECK (blocker_userId != blocked_userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm index để tối ưu query
CREATE INDEX idx_blocker ON BlockedUser(blocker_userId);
CREATE INDEX idx_blocked ON BlockedUser(blocked_userId);
CREATE INDEX idx_blocked_at ON BlockedUser(blockedAt);

-- Kiểm tra bảng đã được tạo
SELECT * FROM BlockedUser LIMIT 1;
