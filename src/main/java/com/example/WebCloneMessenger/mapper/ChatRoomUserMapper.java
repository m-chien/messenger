package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.ChatRoomUserDTO;
import com.example.WebCloneMessenger.Model.ChatRoomUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatRoomUserMapper {

    @Mapping(target = "iduser", expression = "java(chatRoomUser.getIduser() != null ? chatRoomUser.getIduser().getId() : null)")
    @Mapping(target = "idchatroom", expression = "java(chatRoomUser.getIdchatroom() != null ? chatRoomUser.getIdchatroom().getId() : null)")
    @Mapping(target = "lastSeenMessage", expression = "java(chatRoomUser.getLastSeenMessage() != null ? chatRoomUser.getLastSeenMessage().getId() : null)")
    ChatRoomUserDTO toDto(ChatRoomUser chatRoomUser);

    @Mapping(target = "iduser", ignore = true)
    @Mapping(target = "idchatroom", ignore = true)
    @Mapping(target = "lastSeenMessage", ignore = true)
    ChatRoomUser toEntity(ChatRoomUserDTO dto);
}
