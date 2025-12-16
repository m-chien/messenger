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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;

    // Khi client gửi tin nhắn đến /app/chat.send
    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/chatroom/{roomId}")
    public MessageDetailProjection sendMessage(
            @DestinationVariable Integer roomId,
            MessageDTO message,
            Principal principal
    ) {
        if (principal == null) {
            System.err.println("ChatController.sendMessage: principal IS NULL for incoming message, reject");
            throw new AppException(ErrorCode.INVALID_WEBSOCKET_MESSAGE);
        }
        Integer userId = Integer.parseInt(principal.getName());
        System.out.println(userId);
        System.out.println("date send: "+ message.getDateSend());
        message.setUserId(userId);
        message.setChatroom(roomId);
        int idNewMessage = messageService.create(message);
        return messageService.findMessageDetailById(idNewMessage);
    }
}
