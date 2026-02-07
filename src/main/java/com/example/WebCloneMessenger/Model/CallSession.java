package com.example.WebCloneMessenger.Model;

import com.example.WebCloneMessenger.Enum.CallStatus;
import com.example.WebCloneMessenger.Enum.CallType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CallSession")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    // Phòng chat nơi diễn ra cuộc gọi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChatRoomID", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CallerID", nullable = false)
    private User caller;


    // VOICE | VIDEO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CallType callType;

    // RINGING | ACCEPTED | REJECTED | MISSED | ENDED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CallStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
