package com.example.WebCloneMessenger.WebSocket;

import com.example.WebCloneMessenger.DTO.MessageDTO;
import com.example.WebCloneMessenger.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;

    // Khi client gửi tin nhắn đến /app/chat.send
    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/chatroom/{roomId}") // server gửi lại cho tất cả client
    public MessageDTO sendMessage(@DestinationVariable Integer idroom,  MessageDTO message) {
        System.out.println("💬 Received: " + message.getContent() + " in room " + idroom);
        messageService.create(message);
        return message;
    }
}
