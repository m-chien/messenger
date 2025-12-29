package com.example.WebCloneMessenger.Model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "ChatRoom_User")
@EntityListeners(AuditingEntityListener.class)
@IdClass(ChatRoomUserId.class)
@Getter
@Setter
public class ChatRoomUser {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDUser", nullable = false)
    private User iduser;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDChatroom", nullable = false)
    private ChatRoom idchatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LastSeenMessageID")
    private Message lastSeenMessage;

}
