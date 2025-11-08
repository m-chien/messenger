package com.example.WebCloneMessenger.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class FriendRequestDTO {

    private Integer id;

    private OffsetDateTime dateSend;

    @Size(max = 50)
    private String status;

    @NotNull
    private Integer receiver;

    @NotNull
    private Integer sender;

}
