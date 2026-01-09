package com.example.WebCloneMessenger.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MessageResponseDTO {

    private Integer id;
    private String type;
    private String content;
    private Boolean isPin;
    private LocalDateTime dateSend;

    private Integer userId;
    private Boolean isOnline;
    private String userName;
    private String avatarUrl;

    private List<AttachmentDTO> attachments;
}

