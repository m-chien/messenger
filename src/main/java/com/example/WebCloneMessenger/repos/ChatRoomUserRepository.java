package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    ChatRoomUser findFirstByIduserId(Integer id);

    ChatRoomUser findFirstByIdchatroomId(Integer id);

    ChatRoomUser findFirstByLastSeenMessageId(Integer id);

}
