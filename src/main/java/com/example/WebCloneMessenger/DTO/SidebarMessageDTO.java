package com.example.WebCloneMessenger.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SidebarMessageDTO {
    private Integer chatroomId;
    private String lastMessage;
    private LocalDateTime time;
    private Integer senderId;

}

