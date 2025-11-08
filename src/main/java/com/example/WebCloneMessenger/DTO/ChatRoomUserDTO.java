package com.example.WebCloneMessenger.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatRoomUserDTO {

    private Long id;

    @NotNull
    private Integer iduser;

    @NotNull
    private Integer idchatroom;

    private Integer lastSeenMessage;

}
