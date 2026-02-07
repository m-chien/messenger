package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.CallSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallSessionRepository extends JpaRepository<CallSession, Integer> {

    List<CallSession> findByChatRoomId(Integer chatRoomId);

    List<CallSession> findByCallerId(Integer callerId);
}

