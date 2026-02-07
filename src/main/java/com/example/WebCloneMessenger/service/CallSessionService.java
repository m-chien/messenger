package com.example.WebCloneMessenger.service;


import com.example.WebCloneMessenger.Enum.CallStatus;
import com.example.WebCloneMessenger.Enum.CallType;
import com.example.WebCloneMessenger.Exception.AppException;
import com.example.WebCloneMessenger.Exception.ErrorCode;
import com.example.WebCloneMessenger.Model.*;
import com.example.WebCloneMessenger.repos.CallSessionRepository;
import com.example.WebCloneMessenger.repos.ChatRoomRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CallSessionService {

    private final CallSessionRepository callSessionRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // Khi bắt đầu gọi
    public CallSession createCall(Integer chatRoomId, Integer callerId, CallType callType) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        CallSession call = CallSession.builder()
                .chatRoom(chatRoom)
                .caller(caller)
                .callType(callType)
                .status(CallStatus.RINGING)
                .build();

        return callSessionRepository.save(call);
    }


    // Khi accept
    public void acceptCall(Integer callId) {
        CallSession call = getCall(callId);
        call.setStatus(CallStatus.ACCEPTED);
        call.setStartTime(LocalDateTime.now());
        callSessionRepository.save(call);
    }

    // Khi reject
    public void rejectCall(Integer callId) {
        CallSession call = getCall(callId);
        call.setStatus(CallStatus.REJECTED);
        call.setEndTime(LocalDateTime.now());
        callSessionRepository.save(call);
    }

    // Khi end
    public void endCall(Integer callId) {
        CallSession call = getCall(callId);
        call.setStatus(CallStatus.ENDED);
        call.setEndTime(LocalDateTime.now());
        callSessionRepository.save(call);
    }

    // Missed call (timeout)
    public void missedCall(Integer callId) {
        CallSession call = getCall(callId);
        call.setStatus(CallStatus.MISSED);
        call.setEndTime(LocalDateTime.now());
        callSessionRepository.save(call);
    }

    private CallSession getCall(Integer callId) {
        return callSessionRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found: " + callId));
    }
}
