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
public class FriendRequestDetailDTO {

    private Integer requestId;
    private OffsetDateTime dateSend;
    private String status;
    private Integer senderId;
    private String senderName;
    private String senderEmail;
    private String senderAvatarUrl;
    private Boolean senderIsOnline;
}

