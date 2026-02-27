IF EXISTS (SELECT * FROM sys.databases WHERE name = 'Messenger')
BEGIN
    USE master; -- Chuyển sang cơ sở dữ liệu master để có thể xóa được cơ sở dữ liệu khác
    ALTER DATABASE Messenger SET SINGLE_USER WITH ROLLBACK IMMEDIATE; -- Ngắt mọi kết nối
    DROP DATABASE Messenger; -- Xóa cơ sở dữ liệu
END
go
create database Messenger
go
use Messenger

-- Bảng User
CREATE TABLE [User] (
    ID INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(100) NOT NULL,
    Email NVARCHAR(255) UNIQUE NOT NULL,
    password NVARCHAR(255),
    isOnline BIT DEFAULT 0,
    AvatarURL NVARCHAR(500),
	role varchar(50) default 'USER',
	provider_id varchar(255) null,
	provider varchar(20) default 'LOCAL'
);
-- Bảng ChatRoom
CREATE TABLE ChatRoom (
    ID INT PRIMARY KEY IDENTITY(1,1),
    CreatorID INT NOT NULL,
    Name NVARCHAR(200),
    Logo NVARCHAR(500),
    Background NVARCHAR(500),
    Icon NVARCHAR(500),
    Type NVARCHAR(50),
    LastMessageID INT,
    CONSTRAINT FK_ChatRoom_Creator FOREIGN KEY (CreatorID) REFERENCES [User](ID)
);

-- Bảng Message
CREATE TABLE Message (
    ID INT PRIMARY KEY IDENTITY(1,1),
    IDUser INT NOT NULL,
    IdChatroom INT NOT NULL,
    Type NVARCHAR(50),
    DateSend DATETIME DEFAULT GETDATE(),
    Content NVARCHAR(MAX),
    isPin BIT DEFAULT 0,
    ReplyMessageID INT,
    CONSTRAINT FK_Message_User FOREIGN KEY (IDUser) REFERENCES [User](ID),
    CONSTRAINT FK_Message_ChatRoom FOREIGN KEY (IdChatroom) REFERENCES ChatRoom(ID),
    CONSTRAINT FK_Message_Reply FOREIGN KEY (ReplyMessageID) REFERENCES Message(ID)
);

-- Cập nhật Foreign Key cho ChatRoom.LastMessageID
ALTER TABLE ChatRoom
ADD CONSTRAINT FK_ChatRoom_LastMessage FOREIGN KEY (LastMessageID) REFERENCES Message(ID);

-- Bảng ChatRoom_User (Bảng trung gian)
CREATE TABLE ChatRoom_User (
    IDUser INT NOT NULL,
    IDChatroom INT NOT NULL,
    LastSeenMessageID INT,
    PRIMARY KEY (IDUser, IDChatroom),
    CONSTRAINT FK_ChatRoomUser_User FOREIGN KEY (IDUser) REFERENCES [User](ID),
    CONSTRAINT FK_ChatRoomUser_ChatRoom FOREIGN KEY (IDChatroom) REFERENCES ChatRoom(ID),
    CONSTRAINT FK_ChatRoomUser_LastMessage FOREIGN KEY (LastSeenMessageID) REFERENCES Message(ID)
);

-- Bảng Attachment
CREATE TABLE Attachment (
    ID INT PRIMARY KEY IDENTITY(1,1),
    IDMessage INT NOT NULL,
    FileURL NVARCHAR(500),
    FileType NVARCHAR(255),
    FileName NVARCHAR(255),
    FileSize BIGINT,
    CONSTRAINT FK_Attachment_Message FOREIGN KEY (IDMessage) REFERENCES Message(ID) ON DELETE CASCADE
);

-- Bảng Reaction
CREATE TABLE Reaction (
    ID INT PRIMARY KEY IDENTITY(1,1),
    IDUser INT NOT NULL,
    IdMessage INT NOT NULL,
    Type NVARCHAR(50),
    DateSend DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Reaction_User FOREIGN KEY (IDUser) REFERENCES [User](ID),
    CONSTRAINT FK_Reaction_Message FOREIGN KEY (IdMessage) REFERENCES Message(ID) ON DELETE CASCADE
);

-- Bảng FriendRequest
CREATE TABLE FriendRequest (
    ID INT PRIMARY KEY IDENTITY(1,1),
    ReceiverID INT NOT NULL,
    SenderID INT NOT NULL,
    DateSend DATETIME DEFAULT GETDATE(),
    status NVARCHAR(50) DEFAULT 'pending',
    CONSTRAINT FK_FriendRequest_Receiver FOREIGN KEY (ReceiverID) REFERENCES [User](ID),
    CONSTRAINT FK_FriendRequest_Sender FOREIGN KEY (SenderID) REFERENCES [User](ID)
);

-- Bảng BlockList
CREATE TABLE BlockList (
    ID INT PRIMARY KEY IDENTITY(1,1),
    BlockerID INT NOT NULL,
    BlockedID INT NOT NULL,
    BlockedDate DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_BlockList_Blocker FOREIGN KEY (BlockerID) REFERENCES [User](ID),
    CONSTRAINT FK_BlockList_Blocked FOREIGN KEY (BlockedID) REFERENCES [User](ID),
    CONSTRAINT UQ_BlockList UNIQUE (BlockerID, BlockedID)
);

CREATE TABLE CallSession (
    ID INT IDENTITY PRIMARY KEY,

    ChatRoomID INT NOT NULL,
    CallerID INT NOT NULL, 
    CallType NVARCHAR(10) NOT NULL,   -- VOICE | VIDEO
    Status NVARCHAR(20) NOT NULL,     -- RINGING | ACCEPTED | REJECTED | MISSED | ENDED
    StartTime DATETIME NULL,
    EndTime DATETIME NULL,
    CONSTRAINT FK_Call_ChatRoom FOREIGN KEY (ChatRoomID)
        REFERENCES ChatRoom(ID),

    CONSTRAINT FK_Call_Caller FOREIGN KEY (CallerID)
        REFERENCES [User](ID)
);

GO

-- ===================================
-- INSERT DỮ LIỆU MẪU
-- ===================================

-- Insert User
INSERT INTO [User] (Name, Email, password, isOnline, AvatarURL, role) VALUES
(N'Nguyễn Văn An', 'chien@gmail.com', '1', 1, '/img_user/U0001_ava/user_avatar.jpg', 'ADMIN'),
(N'Trần Thị Bình', 'trung@gmail.com', '2', 1, '/img_user/user_avatar/U0002_ava.jpg', 'USER'),
(N'Lê Hoàng Cường', 'cuong.le@email.com', '3', 0, '/img_user/user_avatar/U0003_ava.jpg', 'USER'),
(N'Phạm Thị Dung', 'dung.pham@email.com', '4', 1, '/img_user/user_avatar/U0004_ava.jpg', 'USER'),
(N'Hoàng Văn Em', 'em.hoang@email.com', '5', 0, '/img_user/user_avatar/U005_ava.jpg', 'USER');

-- Insert ChatRoom
INSERT INTO ChatRoom (CreatorID, Name, Logo, Background, Icon, Type) VALUES
(1, N'Nhóm Học Tập', '/img_user/group_avatar/G0001_ava.jpg', 'https://bg.com/blue.jpg', '📚', 'group'),
(2, N'Team Dự Án A', '/img_user/group_avatar/G0002_ava.jpg', 'https://bg.com/green.jpg', '💼', 'group'),
(1, N'Gia Đình', '/img_user/group_avatar/G0003_ava.jpg', 'https://bg.com/warm.jpg', '👨‍👩‍👧‍👦', 'group'),
(3, N'Bạn Thân', '/img_user/group_avatar/G0004_ava.jpg', 'https://bg.com/purple.jpg', '🤝', 'group'),
(4, N'Công Việc', '/img_user/group_avatar/G0005_ava.jpg', 'https://bg.com/gray.jpg', '💻', 'group'),
(5, N'Câu Lạc Bộ Thể Thao', '/img_user/group_avatar/G0006_ava.jpg', 'https://bg.com/red.jpg', '⚽', 'group'),
(1, N'Hội Ẩm Thực', '/img_user/group_avatar/G0007_ava.jpg', 'https://bg.com/orange.jpg', '🍜', 'group'),
(2, N'Du Lịch Việt Nam', 'https://logo.com/travel.png', 'https://bg.com/sky.jpg', '✈️', 'group'),
(3, N'Sách & Văn Học', 'https://logo.com/book.png', 'https://bg.com/brown.jpg', '📖', 'group'),
(4, N'Công Nghệ AI', 'https://logo.com/ai.png', 'https://bg.com/dark.jpg', '🤖', 'group'),
(5, N'Game Thủ', 'https://logo.com/game.png', 'https://bg.com/neon.jpg', '🎮', 'group'),
(1, N'Nhiếp Ảnh', 'https://logo.com/photo.png', 'https://bg.com/black.jpg', '📷', 'group'),
(2, N'Âm Nhạc', 'https://logo.com/music.png', 'https://bg.com/pink.jpg', '🎵', 'group'),
(3, N'Khởi Nghiệp', 'https://logo.com/startup.png', 'https://bg.com/gold.jpg', '💡', 'group'),
(4, N'Thú Cưng', 'https://logo.com/pet.png', 'https://bg.com/cute.jpg', '🐶', 'group'),
(5, N'Phim Ảnh', 'https://logo.com/movie.png', 'https://bg.com/cinema.jpg', '🎬', 'group'),
(1, N'Yoga & Thiền', 'https://logo.com/yoga.png', 'https://bg.com/zen.jpg', '🧘', 'group'),
(2, N'Marketing', 'https://logo.com/marketing.png', 'https://bg.com/business.jpg', '📊', 'group'),
(3, N'Lập Trình Web', 'https://logo.com/web.png', 'https://bg.com/code.jpg', '💻', 'group'),
(4, N'Tiếng Anh Giao Tiếp', 'https://logo.com/english.png', 'https://bg.com/flag.jpg', '🇬🇧', 'group');


-- Insert Message
INSERT INTO Message (IDUser, IdChatroom, Type, Content, isPin) VALUES
(1, 1, 'text', N'Chào mọi người!', 0),
(2, 1, 'text', N'Hôm nay có bài tập gì không?', 0),
(3, 2, 'text', N'Deadline dự án là ngày 15 nhé', 1),
(1, 3, 'text', N'Tối nay về ăn cơm nhé', 0),
(4, 4, 'text', N'Cuối tuần đi chơi không?', 0),
(2, 1, 'text', N'Ai tham gia chạy marathon không?', 0),
(3, 2, 'text', N'Quán phở mới mở ngon lắm', 0),
(4, 3, 'text', N'Du lịch Đà Lạt tháng sau nhé', 1),
(5, 4, 'text', N'Ai đọc sách gì hay giới thiệu với', 0),
(1, 5, 'text', N'ChatGPT 4 mạnh thật', 0),
(2, 1, 'text', N'Chơi PUBG tối nay không?', 0),
(3, 2, 'text', N'Bình minh hôm nay đẹp quá', 0),
(4, 3, 'text', N'Concert cuối tuần ai đi?', 1),
(5, 4, 'text', N'Tìm mentor về kinh doanh', 0),
(1, 5, 'text', N'Chó cưng nhà mình sanh rồi', 0),
(2, 1, 'text', N'Phim Marvel mới xem chưa?', 0),
(3, 2, 'text', N'Buổi tập yoga 6h sáng mai', 1),
(4, 3, 'text', N'Chiến lược content cho Q4', 0),
(5, 4, 'text', N'Học React hay Vue tốt hơn?', 0),
(1, 5, 'text', N'Luyện IELTS speaking cùng nhau', 0),
(1, 1, 'text', N'Mọi người chuẩn bị cho cuộc họp chưa?', 0),
(2, 1, 'text', N'Tài liệu mình đã gửi qua email rồi nhé.', 0),
(1, 1, 'text', N'Cảm ơn bạn. Mình đang xem qua.', 0),
(2, 1, 'text', N'Có vấn đề gì cần thảo luận thêm không?', 0),
(1, 1, 'text', N'Phần phân tích thị trường hơi thiếu chi tiết.', 1),
(2, 1, 'text', N'OK, để mình bổ sung thêm số liệu.', 0),
(1, 1, 'text', N'Khoảng bao giờ thì xong?', 0),
(2, 1, 'text', N'Chắc chắn trước 3 giờ chiều nay.', 0),
(1, 1, 'text', N'Tốt. Vậy 4 giờ họp nhé.', 1),
(2, 1, 'text', N'Đồng ý. Hẹn gặp mọi người.', 0);

-- Update LastMessageID cho ChatRoom
UPDATE ChatRoom SET LastMessageID = 1 WHERE ID = 1;
UPDATE ChatRoom SET LastMessageID = 3 WHERE ID = 2;
UPDATE ChatRoom SET LastMessageID = 4 WHERE ID = 3;
UPDATE ChatRoom SET LastMessageID = 5 WHERE ID = 4;
UPDATE ChatRoom SET LastMessageID = 30 WHERE ID = 1;
UPDATE ChatRoom SET LastMessageID = 12 WHERE ID = 2;
UPDATE ChatRoom SET LastMessageID = 13 WHERE ID = 3;
UPDATE ChatRoom SET LastMessageID = 14 WHERE ID = 4;
UPDATE ChatRoom SET LastMessageID = 15 WHERE ID = 5;

-- Insert ChatRoom_User
INSERT INTO ChatRoom_User (IDUser, IDChatroom, LastSeenMessageID) VALUES
(1, 1, 30),
(2, 1, 2),
(1, 2, 3),
(3, 2, 3),
(1, 3, 4),
(2, 2, 7),
(3, 3, 8),
(4, 4, 9),
(5, 5, 10),
(2, 3, 8),
(3, 4, 9),
(4, 5, 10),
(5, 1, 11),
(2, 4, 14),
(3, 5, 15),
(4, 1, 11),
(5, 2, 12),
(3, 1, 6);

-- Insert Attachment
INSERT INTO Attachment (IDMessage, FileURL, FileType, FileName, FileSize) VALUES
(1, 'https://files.com/doc1.pdf', 'application/pdf', 'bai-tap.pdf', 1024000),
(2, 'https://files.com/img1.jpg', 'image/jpeg', 'anh-minh-hoa.jpg', 512000),
(3, 'https://files.com/doc2.docx', 'application/docx', 'du-an-A.docx', 2048000),
(4, 'https://files.com/video1.mp4', 'video/mp4', 'clip-vui.mp4', 10240000),
(5, 'https://files.com/img2.png', 'image/png', 'screenshot.png', 768000),
(1, 'https://files.com/schedule.pdf', 'application/pdf', 'lich-tap.pdf', 856000),
(2, 'https://files.com/food1.jpg', 'image/jpeg', 'mon-ngon.jpg', 923000),
(3, 'https://files.com/map.png', 'image/png', 'ban-do-dalat.png', 1456000),
(4, 'https://files.com/book.pdf', 'application/pdf', 'sach-hay.pdf', 3024000),
(5, 'https://files.com/ai-guide.docx', 'application/docx', 'huong-dan-ai.docx', 1678000),
(6, 'https://files.com/game.zip', 'application/zip', 'game-mod.zip', 52480000),
(7, 'https://files.com/sunrise.jpg', 'image/jpeg', 'binh-minh.jpg', 2034000),
(8, 'https://files.com/concert.mp4', 'video/mp4', 'live-show.mp4', 45600000),
(9, 'https://files.com/business.pptx', 'application/pptx', 'ke-hoach-kinh-doanh.pptx', 5120000),
(10, 'https://files.com/puppy.jpg', 'image/jpeg', 'cho-con.jpg', 1245000),
(11, 'https://files.com/trailer.mp4', 'video/mp4', 'phim-trailer.mp4', 15680000),
(12, 'https://files.com/yoga.pdf', 'application/pdf', 'bai-tap-yoga.pdf', 2340000),
(13, 'https://files.com/strategy.xlsx', 'application/xlsx', 'chien-luoc.xlsx', 890000),
(14, 'https://files.com/code.zip', 'application/zip', 'source-code.zip', 8960000),
(15, 'https://files.com/vocab.pdf', 'application/pdf', 'tu-vung-ielts.pdf', 1567000);

-- Insert Reaction
INSERT INTO Reaction (IDUser, IdMessage, Type) VALUES
(2, 1, '👍'),
(3, 1, '❤️'),
(1, 2, '😊'),
(4, 3, '👍'),
(2, 5, '🎉'),
(1, 6, '💪'),
(2, 7, '🤤'),
(3, 8, '😍'),
(4, 9, '📚'),
(5, 10, '🚀'),
(1, 11, '🎮'),
(2, 12, '📸'),
(3, 13, '🎶'),
(4, 14, '💼'),
(5, 15, '🐕'),
(1, 16, '🎥'),
(2, 17, '🧘'),
(3, 18, '📈'),
(4, 19, '💻'),
(5, 20, '🇬🇧');

-- Insert FriendRequest
INSERT INTO FriendRequest (ReceiverID, SenderID, status) VALUES
(2, 1, 'accepted'),
(3, 1, 'accepted'),
(4, 2, 'pending'),
(5, 3, 'pending'),
(1, 5, 'rejected'),
(1, 4, 'accepted'),
(2, 5, 'accepted'),
(3, 4, 'accepted'),
(4, 3, 'pending'),
(5, 1, 'accepted'),
(1, 2, 'accepted'),
(2, 4, 'rejected'),
(3, 5, 'pending'),
(4, 5, 'accepted'),
(5, 4, 'pending'),
(1, 3, 'accepted'),
(2, 1, 'accepted'),
(3, 2, 'pending'),
(4, 1, 'rejected'),
(5, 3, 'accepted');

-- Insert BlockList
INSERT INTO BlockList (BlockerID, BlockedID) VALUES
(1, 5),
(2, 3),
(3, 4),
(4, 1),
(5, 2),
(1, 3),
(2, 5),
(3, 1),
(4, 2),
(5, 4),
(1, 4),
(2, 1),
(3, 5),
(4, 3),
(5, 1),
(1, 2),
(2, 4),
(3, 2),
(4, 5),
(5, 3);

go
--trigger sau khi gửi tin nhắn thì người gửi đọc luôn và update tin nhắn mới nhất của room chat đó
create trigger trg_sauKhiGuiTinNhan
on Message
after insert
as
begin
	update c
	set LastMessageID = i.ID
	from chatroom c
		join inserted i on c.ID = i.IDChatRoom
	update crU
	set crU.LastSeenMessageID = i.ID
	from ChatRoom_User crU join inserted i on crU.IDChatroom = i.IdChatroom and crU.IDUser = i.IDUser
end
	
select * from Message
--lấy danh sách các tin  nhắn ở các phòng mà user chưa đọc
select * 
from ChatRoom c
	join ChatRoom_User cu on cu.IDChatroom = c.ID
	join Message m on m.IdChatroom = c.ID
where cu.IDUser = 1 and LastSeenMessageID < LastMessageID
select * from ChatRoom where CreatorID = 1
select IdChatroom , Count(IdChatroom) from Message group by IdChatroom order by IdChatroom asc
select * from ChatRoom_User


--lấy danh sách bạn đã kết bạn
select *
from FriendRequest
where status = 'accepted' and (ReceiverID = 1 or SenderID = 1)
select * from Message where IdChatroom =3
--lấy danh sách các phòng mà user 01 đang có
select * 
from ChatRoom c join ChatRoom_User cu on cu.IDChatroom = c.ID
where cu.IDUser = 1
--lấy thông tin của các phòng chat
SELECT 
    c.ID as idChatroom,
    c.CreatorID, 
    c.Name, 
    c.Logo,

    m.ID as idMessage, 
    m.DateSend, 
    m.Content, 

    u.LastSeenMessageID,

    CASE 
        WHEN EXISTS (
            SELECT 1
            FROM ChatRoom_User cu2
            JOIN [User] u2 ON u2.ID = cu2.IDUser
            WHERE cu2.IDChatroom = c.ID
              AND u2.isOnline = 1
        ) 
        THEN 1 ELSE 0
    END as hasOnlineUser,

    CASE 
        WHEN m.ID > u.LastSeenMessageID THEN 1
        ELSE 0
    END AS isUnread

FROM ChatRoom c 
JOIN Message m 
    ON c.LastMessageID = m.ID
JOIN ChatRoom_User u 
    ON u.IDChatroom = c.ID
WHERE 
    u.IDUser = 1

ORDER BY 
    -- Ưu tiên phòng chưa đọc trước
    CASE 
        WHEN m.ID > u.LastSeenMessageID THEN 1
        ELSE 0	
    END DESC,

    -- Trong nhóm đó, sắp theo tin nhắn mới nhất
    m.DateSend DESC;

--lấy tin nhắn theo phòng
SELECT 
  m.id, m.type, m.content, m.isPin, m.DateSend,
  u.id AS userId, u.isOnline AS isOnline, u.name AS userName, u.AvatarURL AS avatarUrl
FROM message m 
JOIN [user] u ON m.iduser = u.id
WHERE m.idchatroom = 1
ORDER BY m.DateSend ASC

select * from [User]
select * from ChatRoom
select * from Message
select * from Attachment
select * from CallSession
select * from ChatRoom_User where IDChatroom = 1