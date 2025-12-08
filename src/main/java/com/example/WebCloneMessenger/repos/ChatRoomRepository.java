package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.DTO.ChatRoomProjection;
import com.example.WebCloneMessenger.Model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    ChatRoom findFirstByCreatorId(Integer id);

    ChatRoom findFirstByLastMessageId(Integer id);

    @Query(value = """
    select c.*
    from ChatRoom c
    join ChatRoom_User cu on cu.IDChatroom = c.ID
    where cu.IDUser = :userId
""", nativeQuery = true)
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Integer userId);

    @Query(value = """
    SELECT 
        c.ID as idChatroom,
        c.CreatorID as creatorId, 
        c.Name as name, 
        c.Logo as logo,

        m.ID as idMessage, 
        m.DateSend as dateSend, 
        m.Content as content, 

        u.LastSeenMessageID as lastSeenMessageId,

        CASE 
            WHEN EXISTS (
                SELECT 1
                FROM ChatRoom_User cu2
                JOIN [User] u2 ON u2.ID = cu2.IDUser
                WHERE cu2.IDChatroom = c.ID
                  AND u2.IsOnline = 1
                  AND u2.ID <> :userId
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

    WHERE u.IDUser = :userId

    ORDER BY 
        CASE 
            WHEN m.ID > u.LastSeenMessageID THEN 1
            ELSE 0
        END DESC,
        m.DateSend DESC
""", nativeQuery = true)
    List<ChatRoomProjection> findChatRoomsWithLastMessage(@Param("userId") Integer userId);





}
