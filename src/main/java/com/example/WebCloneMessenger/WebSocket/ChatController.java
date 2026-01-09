package com.example.WebCloneMessenger.WebSocket;

import com.example.WebCloneMessenger.DTO.MessageDTO;
import com.example.WebCloneMessenger.DTO.MessageDetailProjection;
import com.example.WebCloneMessenger.Exception.AppException;
import com.example.WebCloneMessenger.Exception.ErrorCode;
import com.example.WebCloneMessenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
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
        int messageId = messageService.create(message);
        MessageDetailProjection savedMessage =
                messageService.findMessageDetailById(messageId);

        // 1) Gửi cho chat window (ai subscribe /topic/chatroom/{roomId})
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + roomId, savedMessage);

        // 2) Delegate notify sidebar cho service (service sẽ dùng messagingTemplate)
        messageService.notifySidebarUsers(roomId, savedMessage);
    }
}
