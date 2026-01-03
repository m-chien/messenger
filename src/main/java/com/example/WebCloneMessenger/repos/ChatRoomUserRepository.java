package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    ChatRoomUser findFirstByIduserId(Integer id);

    ChatRoomUser findFirstByIdchatroomId(Integer id);

    ChatRoomUser findFirstByLastSeenMessageId(Integer id);

    ChatRoomUser findByIdchatroom_IdAndIduser_Id(int chatRoomId, int name);

    boolean existsByIduser_Id(Integer id);
    boolean existsByIdchatroom_Id(Integer chatroomId);
    boolean existsByLastSeenMessage_Id(Integer messageId);

    @Query("""
        select cru.iduser.id
        from ChatRoomUser cru
        where cru.idchatroom.id = :roomId
    """)
    List<Integer> findUserIdsByChatroom(@Param("roomId") Integer roomId);
}
