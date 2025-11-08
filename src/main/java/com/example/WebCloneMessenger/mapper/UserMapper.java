package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user);

    User toEntity(UserDTO dto);
}
