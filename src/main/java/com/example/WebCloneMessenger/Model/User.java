package com.example.WebCloneMessenger.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "User")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 6
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private Boolean isOnline;

    @Column(length = 500)
    private String avatarUrl;

    @OneToMany(mappedBy = "creator")
    private Set<ChatRoom> creatorChatRooms = new HashSet<>();

    @OneToMany(mappedBy = "iduser")
    private Set<Message> iduserMessages = new HashSet<>();

    @OneToMany(mappedBy = "iduser")
    private Set<ChatRoomUser> iduserChatRoomUsers = new HashSet<>();

    @OneToMany(mappedBy = "iduser")
    private Set<Reaction> iduserReactions = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<FriendRequest> receiverFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<FriendRequest> senderFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "blocker")
    private Set<BlockList> blockerBlockLists = new HashSet<>();

    @OneToMany(mappedBy = "blocked")
    private Set<BlockList> blockedBlockLists = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime2")
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime2")
    private OffsetDateTime lastUpdated;

}
