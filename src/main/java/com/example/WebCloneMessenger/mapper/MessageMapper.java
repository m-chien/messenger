package com.example.WebCloneMessenger.mapper;
import com.example.WebCloneMessenger.DTO.MessageDTO;
import org.mapstruct.*;
import com.example.WebCloneMessenger.Model.*;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    // Lấy id từ các quan hệ (User, ChatRoom, Message)
    @Mapping(target = "userId", expression = "java(message.getIdUser() != null ? message.getIdUser().getId() : null)")
    @Mapping(target = "chatroom", expression = "java(message.getChatroom() != null ? message.getChatroom().getId() : null)")
    @Mapping(target = "replyMessage", expression = "java(message.getReplyMessage() != null ? message.getReplyMessage().getId() : null)")
    MessageDTO toDto(Message message);

    // Ngược lại: ignore vì DTO chỉ có id (không có entity)
    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "chatroom", ignore = true)
    @Mapping(target = "replyMessage", ignore = true)
    Message toEntity(MessageDTO dto);
}

