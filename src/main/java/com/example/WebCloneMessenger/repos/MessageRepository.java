package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Integer> {

    Message findFirstByIdUserId(Integer userId); // ✅ Đúng

    Message findFirstByChatroomId(Integer id);

    Message findFirstByReplyMessageIdAndIdNot(Integer id, Integer currentId);

}
