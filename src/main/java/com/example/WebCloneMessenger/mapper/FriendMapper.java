package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.FriendDTO;
import com.example.WebCloneMessenger.DTO.FriendDetailDTO;
import com.example.WebCloneMessenger.Model.Friend;
import com.example.WebCloneMessenger.Model.FriendId;
import com.example.WebCloneMessenger.Model.User;
import org.springframework.stereotype.Component;

@Component
public class FriendMapper {

    public FriendDTO toDTO(Friend friend) {
        if (friend == null) return null;

        return new FriendDTO(
                friend.getId().getUserID1(),
                friend.getId().getUserID2(),
                friend.getCreatedDate()
        );
    }

    public Friend toEntity(FriendDTO dto) {
        if (dto == null) return null;

        Friend friend = new Friend();
        friend.setId(new FriendId(dto.getUserID1(), dto.getUserID2()));
        friend.setCreatedDate(dto.getCreatedDate());
        return friend;
    }

    public FriendDetailDTO toDetailDTO(Friend friend, Integer currentUserId) {
        if (friend == null) return null;

        User friendUser = currentUserId.equals(friend.getId().getUserID1())
                ? friend.getUser2()
                : friend.getUser1();

        if (friendUser == null) return null;

        FriendDetailDTO dto = new FriendDetailDTO();
        dto.setUserId(friendUser.getId());
        dto.setName(friendUser.getName());
        dto.setEmail(friendUser.getEmail());
        dto.setAvatarUrl(friendUser.getAvatarUrl());
        dto.setIsOnline(friendUser.getIsOnline());
        dto.setFriendCreatedDate(friend.getCreatedDate());
        return dto;
    }
}

