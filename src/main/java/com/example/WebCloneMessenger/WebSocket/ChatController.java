package com.example.WebCloneMessenger.WebSocket;

import com.example.WebCloneMessenger.DTO.MessageDTO;
import com.example.WebCloneMessenger.DTO.MessageResponseDTO;
import com.example.WebCloneMessenger.Exception.AppException;
import com.example.WebCloneMessenger.Exception.ErrorCode;
import com.example.WebCloneMessenger.service.MessageService;
import com.example.WebCloneMessenger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // Khi client gửi tin nhắn đến /app/chat.send
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(
            @DestinationVariable Integer roomId,
            MessageDTO message,
            Principal principal
    ) {
        if (principal == null) {
            throw new AppException(ErrorCode.INVALID_WEBSOCKET_MESSAGE);
        }

        Integer senderId = Integer.parseInt(principal.getName());
        message.setUserId(senderId);
        message.setChatroom(roomId);
        System.out.println("xem thông tin message nhận được: " + message.getAttachments());

        // Lưu message
        MessageResponseDTO savedMessage = messageService.create(message);

        // 1) Gửi cho chat window (ai subscribe /topic/chatroom/{roomId})
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + roomId, savedMessage);

        // 2) Delegate notify sidebar cho service (service sẽ dùng messagingTemplate)
        messageService.notifySidebarUsers(roomId, savedMessage);
    }

    @EventListener
    public void handleconnectEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();
        if(principal != null)
        {
            Integer userId = Integer.parseInt(principal.getName());
            userService.setActiveStatus(userId, true);
            System.out.println("🟢 User ONLINE: " + userId);
        }

    }

    @EventListener
    public void handledisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();
        if(principal != null)
        {
            Integer userId = Integer.parseInt(principal.getName());
            userService.setActiveStatus(userId, false);
            System.out.println("🟢 User ONLINE: " + userId);
        }
    }
}
