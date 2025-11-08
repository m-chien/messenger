package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.ChatRoomDTO;
import com.example.WebCloneMessenger.DTO.MessageDTO;
import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {

    @Mapping(target = "idUser", source = "idUser", qualifiedByName = "mapUserToId")
    @Mapping(target = "chatroom", source = "chatroom", qualifiedByName = "mapChatRoomToId")
    @Mapping(target = "replyMessage", source = "replyMessage", qualifiedByName = "mapReplyMessageToId")
    MessageDTO toDto(Message message);

    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "chatroom", ignore = true)
    @Mapping(target = "replyMessage", ignore = true)
    Message toEntity(MessageDTO dto);

    // ✅ Các hàm phụ đảm bảo compile-time an toàn
    @Named("mapUserToId")
    public static Integer mapUserToId(User user) {
        return user != null ? user.getId() : null;
    }

    @Named("mapChatRoomToId")
    public static Integer mapChatRoomToId(ChatRoom chatRoom) {
        return chatRoom != null ? chatRoom.getId() : null;
    }

    @Named("mapReplyMessageToId")
    public static Integer mapReplyMessageToId(Message reply) {
        return reply != null ? reply.getId() : null;
    }
}
