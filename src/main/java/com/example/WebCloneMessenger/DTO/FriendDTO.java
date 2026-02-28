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
public class FriendDTO {

    private Integer userID1;
    private Integer userID2;
    private OffsetDateTime createdDate;
}

