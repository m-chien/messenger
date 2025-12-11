package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.DTO.MessageDetailProjection;
import com.example.WebCloneMessenger.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Integer> {

    Message findFirstByIdUserId(Integer userId); // ✅ Đúng

    Message findFirstByChatroomId(Integer id);

    Message findFirstByReplyMessageIdAndIdNot(Integer id, Integer currentId);


    @Query(value = "SELECT " +
            "m.id, m.type, m.content, m.isPin, m.DateSend, " +
            // Đảm bảo tên Alias khớp với tên Getter trong Interface (userId, userName)
            "u.id AS userId, u.isOnline AS isOnline, u.name AS userName, u.AvatarURL AS avatarUrl " +
            "FROM message m JOIN [user] u ON m.iduser = u.id " +
            "WHERE m.idchatroom = :chatRoomId " +
            "ORDER BY m.DateSend ASC",
            nativeQuery = true)
        // ⚠️ Kiểu trả về là List của Interface Projection
    List<MessageDetailProjection> findMessageDetailsByChatRoomId(@Param("chatRoomId") Integer chatRoomId);


    @Query(value = "SELECT " +
            "m.id, m.type, m.content, m.isPin, m.DateSend, " +
            "u.id AS userId, u.isOnline AS isOnline, u.name AS userName, u.AvatarURL AS avatarUrl " +
            "FROM message m JOIN [user] u ON m.iduser = u.id " +
            "WHERE m.id = :messageId",
            nativeQuery = true)
    MessageDetailProjection findMessageDetailById(@Param("messageId") Integer messageId);

}
