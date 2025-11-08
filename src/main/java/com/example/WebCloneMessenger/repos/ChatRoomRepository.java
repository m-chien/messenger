package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    ChatRoom findFirstByCreatorId(Integer id);

    ChatRoom findFirstByLastMessageId(Integer id);

}
