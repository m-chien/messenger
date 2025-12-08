package com.example.WebCloneMessenger.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface ChatRoomProjection {
    Integer getIdChatroom();
    Integer getCreatorId();
    String getName();
    String getLogo();

    Integer getIdMessage();
    String getContent();
    LocalDateTime getDateSend();

    Integer getLastSeenMessageId();
    Integer getHasOnlineUser();
    Integer getIsUnread();
}
