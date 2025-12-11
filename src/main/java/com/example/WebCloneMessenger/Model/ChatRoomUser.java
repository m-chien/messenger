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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "ChatRoomUser")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ChatRoomUser {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iduser_id", nullable = false)
    private User iduser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idchatroom_id", nullable = false)
    private ChatRoom idchatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_seen_message_id")
    private Message lastSeenMessage;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime2")
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime2")
    private OffsetDateTime lastUpdated;

}
