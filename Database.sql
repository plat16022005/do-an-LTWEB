-- ==========================
-- TẠO CƠ SỞ DỮ LIỆU
-- ==========================
DROP DATABASE IF EXISTS AloUTEDB;
CREATE DATABASE AloUTEDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE AloUTEDB;

-- ==========================
-- 1. USERS - Người dùng
-- ==========================
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Email VARCHAR(255) UNIQUE,
    PasswordHash VARCHAR(255),
    FullName VARCHAR(100),
    Location VARCHAR(100),
    Birthday DATE,
    Gender VARCHAR(20),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    Avatar VARCHAR(255),
    IsActivated BOOLEAN DEFAULT TRUE,
    Role VARCHAR(20),
    IsLocked BIT DEFAULT 0,
    LockedReason NVARCHAR(255),
    LockedAt DATETIME
) ENGINE=InnoDB;

-- ==========================
-- 2. FRIENDS - Kết bạn
-- ==========================
CREATE TABLE Friends (
    FriendID INT AUTO_INCREMENT PRIMARY KEY,
    UserID1 INT,
    UserID2 INT,
    Status VARCHAR(20),
    RequestDate DATETIME,
    AcceptDate DATETIME,
    FOREIGN KEY (UserID1) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (UserID2) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 3. POSTS - Bài đăng
-- ==========================
CREATE TABLE Posts (
    PostID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    Content TEXT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    LikesCount INT DEFAULT 0,
    ShareCount INT DEFAULT 0,
    CommentsCount INT DEFAULT 0,
    Visibility VARCHAR(20) DEFAULT 'public',
    IsDeleted BOOLEAN DEFAULT FALSE,
    Status VARCHAR(20) DEFAULT 'approved', -- pending / approved / rejected
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 4. COMMENTS - Bình luận
-- ==========================
CREATE TABLE Comments (
    CommentID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    PostID INT,
    Content VARCHAR(1000),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsDeleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 5. LIKES - Lượt thích
-- ==========================
CREATE TABLE Likes (
    LikeID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    PostID INT,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE,
    UNIQUE(UserID, PostID)
) ENGINE=InnoDB;

-- ==========================
-- 6. SHARES - Chia sẻ
-- ==========================
CREATE TABLE Shares (
    ShareID INT AUTO_INCREMENT PRIMARY KEY,
    PostID INT,
    UserID INT,
    Content VARCHAR(1000),
    ShareAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    Visibility VARCHAR(20) DEFAULT 'public',
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 7. MESSAGES - Tin nhắn
-- ==========================
CREATE TABLE Messages (
    MessageID INT AUTO_INCREMENT PRIMARY KEY,
    SenderID INT,
    ReceiverID INT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    Content VARCHAR(1000),
    IsRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (SenderID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ReceiverID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 8. ATTACHMENTS - Tệp đính kèm
-- ==========================
CREATE TABLE Attachments (
    AttachmentID INT AUTO_INCREMENT PRIMARY KEY,
    PostID INT,
    MessageID INT,
    FileURL VARCHAR(255),
    FileType VARCHAR(50),
    UploadedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE,
    FOREIGN KEY (MessageID) REFERENCES Messages(MessageID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 9. REPORTS - Báo cáo
-- ==========================
CREATE TABLE Reports (
    ReportID INT AUTO_INCREMENT PRIMARY KEY,
    ReporterID INT,
    ReportedID INT,
    Type VARCHAR(50),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    Status VARCHAR(20) DEFAULT 'pending',
    Reason VARCHAR(500),
    ResolutionStatus VARCHAR(20) DEFAULT 'pending', -- pending / resolved / rejected
    ResolvedBy INT,
    ResolvedAt DATETIME,
    FOREIGN KEY (ReporterID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ReportedID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ResolvedBy) REFERENCES Users(UserID) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ==========================
-- 10. NOTIFICATIONS - Thông báo
-- ==========================
CREATE TABLE Notifications (
    NotificationID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    Type VARCHAR(50),
    RelatedID INT,
    Content VARCHAR(255),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 11. GROUPS - Nhóm
-- ==========================
CREATE TABLE GroupsUTE (
    GroupID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100),
    UserID INT,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CreatedBy INT,
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 12. USERGROUPS - Thành viên nhóm
-- ==========================
CREATE TABLE UserGroups (
    UserGroupID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    GroupID INT,
    JoinedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    RoleInGroup VARCHAR(20),
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (GroupID) REFERENCES GroupsUTE(GroupID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 13. BLOCKED USERS - Danh sách chặn
-- ==========================
CREATE TABLE BlockedUsers (
    BlockedID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    BlockedUserID INT,
    BlockedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (BlockedUserID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 14. AUDIT LOGS - Nhật ký hoạt động
-- ==========================
CREATE TABLE AuditLogs (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT,
    Action VARCHAR(100),
    Details VARCHAR(500),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ==========================
-- 15. STATISTICS - Thống kê
-- ==========================
CREATE TABLE Statistics (
    StatID INT AUTO_INCREMENT PRIMARY KEY,
    Type VARCHAR(50) NOT NULL,
    Value BIGINT NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ==========================
-- 16. POST MODERATION - Kiểm duyệt bài viết
-- ==========================
CREATE TABLE PostModeration (
    ModerationID INT AUTO_INCREMENT PRIMARY KEY,
    PostID INT NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'pending',
    ModeratorID INT,
    ReviewedAt DATETIME,
    Reason NVARCHAR(500),
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE,
    FOREIGN KEY (ModeratorID) REFERENCES Users(UserID) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ==========================
-- 17. WARNINGS - Cảnh cáo vi phạm
-- ==========================
CREATE TABLE Warnings (
    WarningID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    Reason NVARCHAR(500),
    Type VARCHAR(50),
    Count INT DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IssuedBy INT,
    Status VARCHAR(20) DEFAULT 'active',
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (IssuedBy) REFERENCES Users(UserID) ON DELETE SET NULL
) ENGINE=InnoDB;
-- ==========================
-- 18. GROUP MESSAGES - Tin nhắn trong nhóm
-- ==========================
CREATE TABLE GroupMessages (
    GroupMessageID INT AUTO_INCREMENT PRIMARY KEY,
    GroupID INT NOT NULL,
    SenderID INT NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    Content VARCHAR(1000),
    IsRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (GroupID) REFERENCES GroupsUTE(GroupID) ON DELETE CASCADE,
    FOREIGN KEY (SenderID) REFERENCES Users(UserID) ON DELETE CASCADE
) ENGINE=InnoDB;
-- ==========================
-- 19. GROUP ATTACHMENTS - File đính kèm tin nhắn nhóm
-- ==========================
CREATE TABLE GroupAttachments (
    AttachmentID INT AUTO_INCREMENT PRIMARY KEY,
    GroupMessageID INT,
    FileURL VARCHAR(255),
    FileType VARCHAR(50),
    UploadedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (GroupMessageID) REFERENCES GroupMessages(GroupMessageID) ON DELETE CASCADE
) ENGINE=InnoDB;

ALTER TABLE users
ADD COLUMN NameUser NVARCHAR(50) AFTER PasswordHash;

ALTER TABLE users
ADD COLUMN Background NVARCHAR(255) AFTER Avatar,
ADD COLUMN Introduce NVARCHAR(255) AFTER Gender;

ALTER TABLE shares
ADD COLUMN Content VARCHAR(1000);

ALTER TABLE comments
ADD COLUMN LikeCount INT DEFAULT 0,
ADD COLUMN ParentCommentID INT NULL AFTER PostID;

ALTER TABLE Notifications
ADD COLUMN ActorAvatar VARCHAR(255) NULL AFTER Content;


ALTER TABLE GroupsUTE
DROP COLUMN UserID;

-- 2. Đổi tên cột Name thành NameGroup
ALTER TABLE GroupsUTE
CHANGE COLUMN Name NameGroup VARCHAR(100) NOT NULL;

-- 3. Thêm cột Avatar
ALTER TABLE GroupsUTE
ADD COLUMN Avatar VARCHAR(255) AFTER NameGroup;
