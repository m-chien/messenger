package com.example.WebCloneMessenger.DTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface MessageDetailProjection {
    // Thuộc tính từ Message
    Integer getId();
    String getType();
    String getContent();
    Boolean getIsPin();
    LocalDateTime getDateSend();

    // Thuộc tính từ User
    Integer getUserId(); // Ánh xạ từ u.id (ALIAS)
    Boolean getIsOnline();
    String getUserName(); // Ánh xạ từ u.name (ALIAS)
    String getAvatarUrl();
}