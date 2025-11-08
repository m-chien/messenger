package com.example.WebCloneMessenger.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "Message")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Message {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 21
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer id;

    @Column(length = 50)
    private String type;

    @Column(columnDefinition = "datetime2")
    private OffsetDateTime dateSend;

    @Column(columnDefinition = "varchar(max)")
    private String content;

    @Column
    private Boolean isPin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDUser", nullable = false)
    @NotNull
    private User idUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdChatroom", nullable = false)
    @NotNull
    private ChatRoom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReplyMessageID")
    private Message replyMessage;

    @OneToMany(mappedBy = "replyMessage")
    private Set<Message> replyMessageMessages = new HashSet<>();

    @OneToMany(mappedBy = "lastMessage")
    private Set<ChatRoom> lastMessageChatRooms = new HashSet<>();

    @OneToMany(mappedBy = "lastSeenMessage")
    private Set<ChatRoomUser> lastSeenMessageChatRoomUsers = new HashSet<>();

    @OneToMany(mappedBy = "idmessage")
    private Set<Attachment> idmessageAttachments = new HashSet<>();

    @OneToMany(mappedBy = "message")
    private Set<Reaction> messageReactions = new HashSet<>();
}
