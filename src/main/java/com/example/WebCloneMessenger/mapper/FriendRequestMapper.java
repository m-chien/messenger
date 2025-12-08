package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.FriendRequestDTO;
import com.example.WebCloneMessenger.Model.FriendRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {
    @Mapping(source = "receiver.id", target = "receiver")
    @Mapping(source = "sender.id", target = "sender")
    FriendRequestDTO friendRequestToFriendRequestDTO(FriendRequest friendRequest);
}
