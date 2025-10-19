-- Script SQL để tạo bảng BlockedUsers cho chức năng chặn người dùng
-- Chạy script này trong MySQL để thêm bảng mới vào database aloutedb

USE aloutedb;

-- Tạo bảng BlockedUsers
CREATE TABLE IF NOT EXISTS BlockedUsers (
    BlockID INT AUTO_INCREMENT PRIMARY KEY,
    BlockerID INT NOT NULL,
    BlockedID INT NOT NULL,
    BlockedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_blocker FOREIGN KEY (BlockerID) REFERENCES Users(UserID) ON DELETE CASCADE,
    CONSTRAINT fk_blocked FOREIGN KEY (BlockedID) REFERENCES Users(UserID) ON DELETE CASCADE,
    
    -- Đảm bảo không trùng lặp: một người chỉ có thể chặn người khác 1 lần
    CONSTRAINT unique_block UNIQUE (BlockerID, BlockedID),
    
    -- Đảm bảo không tự chặn mình
    CONSTRAINT check_not_self CHECK (BlockerID != BlockedID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo index để tăng tốc độ truy vấn
CREATE INDEX idx_blocker ON BlockedUsers(BlockerID);
CREATE INDEX idx_blocked ON BlockedUsers(BlockedID);
CREATE INDEX idx_blocked_at ON BlockedUsers(BlockedAt);

-- Kiểm tra bảng đã tạo thành công
SELECT 'Bảng BlockedUsers đã được tạo thành công!' AS Status;
