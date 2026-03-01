package com.example.WebCloneMessenger.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendDetailDTO {

    private Integer userId;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
    private Boolean isOnline;
    private OffsetDateTime friendCreatedDate;
}

