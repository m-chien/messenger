package com.example.WebCloneMessenger.mapper;
import com.example.WebCloneMessenger.DTO.MessageDTO;
import org.mapstruct.*;
import com.example.WebCloneMessenger.Model.*;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "idUser", expression = "java(message.getIduser() != null ? message.getIduser().getId() : 'null')")
    @Mapping(target = "chatroom", expression = "java(message.getChatroom() != null ? message.getChatroom().getId() : `null`)")
    @Mapping(target = "replyMessage", expression = "java(message.getReplyMessage() != null ? message.getReplyMessage().getId() : `null`)")
    MessageDTO toDto(Message message);

    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "chatroom", ignore = true)
    @Mapping(target = "replyMessage", ignore = true)
    Message toEntity(MessageDTO dto);
}
