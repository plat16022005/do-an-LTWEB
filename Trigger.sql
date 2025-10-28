DELIMITER $$

DROP TRIGGER IF EXISTS trg_Comments_AfterInsert;

DELIMITER $$

CREATE TRIGGER trg_Comments_AfterInsert
AFTER INSERT ON Comments
FOR EACH ROW
BEGIN
    IF NEW.ParentCommentID IS NULL THEN
        UPDATE Posts
        SET CommentsCount = CommentsCount + 1
        WHERE PostID = NEW.PostID;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

-- ==========================
-- Cập nhật đếm comment gốc
-- ==========================
DROP TRIGGER IF EXISTS trg_Comments_AfterInsert;
CREATE TRIGGER trg_Comments_AfterInsert
AFTER INSERT ON Comments
FOR EACH ROW
BEGIN
    IF NEW.ParentCommentID IS NULL THEN
        UPDATE Posts
        SET CommentsCount = CommentsCount + 1
        WHERE PostID = NEW.PostID;
    END IF;
END$$

-- ==========================
-- COMMENT Notification
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnComment;
CREATE TRIGGER trg_Notifications_OnComment
AFTER INSERT ON Comments
FOR EACH ROW
BEGIN
    DECLARE post_owner INT;
    DECLARE commenter_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);

    SELECT UserID INTO post_owner FROM Posts WHERE PostID = NEW.PostID;
    SELECT FullName, Avatar INTO commenter_name, actor_avatar FROM Users WHERE UserID = NEW.UserID;

    IF post_owner <> NEW.UserID THEN
        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        VALUES (
            post_owner,
            'COMMENT',
            NEW.PostID,
            CONCAT(commenter_name, ' đã bình luận vào bài viết của bạn'),
            actor_avatar
        );
    END IF;
END$$

-- ==========================
-- LIKE Notification
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnLike;
CREATE TRIGGER trg_Notifications_OnLike
AFTER INSERT ON Likes
FOR EACH ROW
BEGIN
    DECLARE post_owner INT;
    DECLARE liker_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);

    SELECT UserID INTO post_owner FROM Posts WHERE PostID = NEW.PostID;
    SELECT FullName, Avatar INTO liker_name, actor_avatar FROM Users WHERE UserID = NEW.UserID;

    IF post_owner <> NEW.UserID THEN
        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        VALUES (
            post_owner,
            'LIKE',
            NEW.PostID,
            CONCAT(liker_name, ' đã thích bài viết của bạn'),
            actor_avatar
        );
    END IF;
END$$

-- ==========================
-- UNLIKE Notification (xóa)
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnUnlike;
CREATE TRIGGER trg_Notifications_OnUnlike
AFTER DELETE ON Likes
FOR EACH ROW
BEGIN
    DELETE FROM Notifications
    WHERE Type = 'LIKE'
      AND RelatedID = OLD.PostID
      AND UserID = (SELECT UserID FROM Posts WHERE PostID = OLD.PostID);
END$$

-- ==========================
-- SHARE Notification
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnShare;
CREATE TRIGGER trg_Notifications_OnShare
AFTER INSERT ON Shares
FOR EACH ROW
BEGIN
    DECLARE post_owner INT;
    DECLARE sharer_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);

    SELECT UserID INTO post_owner FROM Posts WHERE PostID = NEW.PostID;
    SELECT FullName, Avatar INTO sharer_name, actor_avatar FROM Users WHERE UserID = NEW.UserID;

    IF post_owner <> NEW.UserID THEN
        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        VALUES (
            post_owner,
            'SHARE',
            NEW.PostID,
            CONCAT(sharer_name, ' đã chia sẻ bài viết của bạn'),
            actor_avatar
        );
    END IF;
END$$

-- ==========================
-- FRIEND REQUEST Notification
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnFriendRequest;
CREATE TRIGGER trg_Notifications_OnFriendRequest
AFTER INSERT ON Friends
FOR EACH ROW
BEGIN
    DECLARE sender_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);

    SELECT FullName, Avatar INTO sender_name, actor_avatar
    FROM Users WHERE UserID = NEW.UserID1;

    IF NEW.Status = 'pending' THEN
        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        VALUES (
            NEW.UserID2,
            'FRIEND_REQUEST',
            NEW.UserID1,
            CONCAT(sender_name, ' đã gửi lời mời kết bạn'),
            actor_avatar
        );
    END IF;
END$$

-- ==========================
-- FRIEND ACCEPT Notification
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnFriendAccept;
CREATE TRIGGER trg_Notifications_OnFriendAccept
AFTER UPDATE ON Friends
FOR EACH ROW
BEGIN
    DECLARE receiver_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);

    SELECT FullName, Avatar INTO receiver_name, actor_avatar
    FROM Users WHERE UserID = NEW.UserID2;

    IF OLD.Status = 'pending' AND NEW.Status = 'accepted' THEN
        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        VALUES (
            NEW.UserID1,
            'FRIEND_ACCEPT',
            NEW.UserID2,
            CONCAT(receiver_name, ' đã chấp nhận lời mời kết bạn'),
            actor_avatar
        );
    END IF;
END$$

-- ==========================
-- MESSAGE Notification
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnMessage;
DELIMITER $$
CREATE TRIGGER trg_Notifications_OnMessage
AFTER INSERT ON Messages
FOR EACH ROW
BEGIN
    DECLARE sender_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);
    DECLARE last_msg_time DATETIME;

    -- Lấy thông tin người gửi
    SELECT FullName, Avatar 
    INTO sender_name, actor_avatar
    FROM Users 
    WHERE UserID = NEW.SenderID;

    -- Lấy thời gian tin nhắn cuối cùng giữa hai người (theo chiều người gửi → người nhận)
    SELECT MAX(CreatedAt)
    INTO last_msg_time
    FROM Messages
    WHERE SenderID = NEW.SenderID 
      AND ReceiverID = NEW.ReceiverID
      AND MessageID <> NEW.MessageID;  -- tránh lấy chính tin nhắn mới chèn

    -- Nếu chưa có tin nhắn trước đó hoặc khoảng cách >= 5 phút thì mới tạo thông báo
    IF last_msg_time IS NULL OR TIMESTAMPDIFF(MINUTE, last_msg_time, NEW.CreatedAt) >= 5 THEN
        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        VALUES (
            NEW.ReceiverID,
            'MESSAGE',
            NEW.SenderID,
            CONCAT(sender_name, ' đã gửi cho bạn một tin nhắn'),
            actor_avatar
        );
    END IF;
END$$
DELIMITER ;



-- ==========================
-- FRIEND POST Notification (sau duyệt bài)
-- ==========================
DROP TRIGGER IF EXISTS trg_Notifications_OnFriendPostApproved;
CREATE TRIGGER trg_Notifications_OnFriendPostApproved
AFTER UPDATE ON Posts
FOR EACH ROW
BEGIN
    DECLARE poster_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);

    IF OLD.Status = 'pending' AND NEW.Status = 'approved' THEN
        SELECT FullName, Avatar INTO poster_name, actor_avatar
        FROM Users WHERE UserID = NEW.UserID;

        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        SELECT f.UserID2, 'FRIEND_POST', NEW.PostID,
               CONCAT(poster_name, ' đã đăng một bài viết mới'),
               actor_avatar
        FROM Friends f
        WHERE f.UserID1 = NEW.UserID AND f.Status = 'accepted';

        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        SELECT f.UserID1, 'FRIEND_POST', NEW.PostID,
               CONCAT(poster_name, ' đã đăng một bài viết mới'),
               actor_avatar
        FROM Friends f
        WHERE f.UserID2 = NEW.UserID AND f.Status = 'accepted';
    END IF;
END$$

DELIMITER ;

DROP TRIGGER IF EXISTS trg_Notifications_OnGroupMessage;
DELIMITER $$
CREATE TRIGGER trg_Notifications_OnGroupMessage
AFTER INSERT ON GroupMessages
FOR EACH ROW
BEGIN
    DECLARE sender_name VARCHAR(100);
    DECLARE actor_avatar VARCHAR(255);
    DECLARE group_name VARCHAR(100);
    DECLARE last_group_msg_time DATETIME;

    -- Lấy thông tin người gửi
    SELECT FullName, Avatar
    INTO sender_name, actor_avatar
    FROM Users
    WHERE UserID = NEW.SenderID;

    -- Lấy tên nhóm
    SELECT NameGroup
    INTO group_name
    FROM GroupsUTE
    WHERE GroupID = NEW.GroupID;

    -- Lấy thời gian gửi tin nhóm gần nhất của người gửi
    SELECT MAX(CreatedAt)
    INTO last_group_msg_time
    FROM GroupMessages
    WHERE GroupID = NEW.GroupID
      AND SenderID = NEW.SenderID
      AND GroupMessageID <> NEW.GroupMessageID;

    -- Chỉ tạo thông báo nếu không có tin nhắn trước hoặc cách >= 5 phút
    IF last_group_msg_time IS NULL 
       OR TIMESTAMPDIFF(MINUTE, last_group_msg_time, NEW.CreatedAt) >= 5 THEN

        INSERT INTO Notifications (UserID, Type, RelatedID, Content, ActorAvatar)
        SELECT ug.UserID,
               'GROUP_MESSAGE',
               NEW.GroupID,
               CONCAT(sender_name, ' đã gửi tin nhắn trong nhóm ', group_name),
               actor_avatar
        FROM UserGroups ug
        WHERE ug.GroupID = NEW.GroupID
          AND ug.UserID <> NEW.SenderID;

    END IF;
END$$
DELIMITER ;



