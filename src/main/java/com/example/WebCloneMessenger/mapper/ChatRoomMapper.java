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

    @Mapping(source = "creator.id", target = "creator")
    @Mapping(source = "lastMessage.id",target = "lastMessage")
    ChatRoomDTO toDto(ChatRoom chatRoom);

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    ChatRoom toEntity(ChatRoomDTO dto);
}

