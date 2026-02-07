package com.example.WebCloneMessenger.WebSocket;

import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.ChatRoomUser;
import com.example.WebCloneMessenger.repos.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class CallController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/call")
    public void handleCallSignal(
            Map<String, Object> payload,
            Principal principal
    ) {
        if (principal == null) return;
        System.out.println("📞 CALL SIGNAL RECEIVED: " + payload);

        Integer fromUserId = Integer.parseInt(principal.getName());
        
        // Check if chatRoomId is present
        Object chatRoomIdObj = payload.get("chatRoomId");
        if (chatRoomIdObj == null) {
            System.err.println("❌ Error: chatRoomId is missing in call signal payload");
            return;
        }

        Integer chatRoomId = (Integer) chatRoomIdObj;
        ChatRoom chatRoom = chatRoomRepository.findWithUsersById(chatRoomId);

        if (chatRoom == null) {
            // Fallback if not found with users (should not happen if ID exists)
             chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        }

        if (chatRoom == null) {
            System.err.println("❌ Error: ChatRoom not found for ID: " + chatRoomId);
            return;
        }

        payload.put("fromUserId", fromUserId);

        Set<ChatRoomUser> participants = chatRoom.getIdchatroomChatRoomUsers();
        
        for (ChatRoomUser participant : participants) {
            Integer userId = participant.getIduser().getId();
            
            // Skip sending to self
            if (userId.equals(fromUserId)) {
                continue;
            }

            System.out.println("📞 CALL SIGNAL push to user " + userId + ": " + payload);

            // Send to each participant
            messagingTemplate.convertAndSend(
                    "/topic/user/" + userId + "/call",
                    payload
            );
        }
    }
}
