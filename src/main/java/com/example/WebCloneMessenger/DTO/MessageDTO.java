package com.example.WebCloneMessenger.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class MessageDTO {

    private Integer id;

    @Size(max = 50)
    private String type;

    private OffsetDateTime dateSend;

    private String content;

    @JsonProperty("isPin")
    private Boolean isPin;

    @NotNull
    private Integer idUser;

    @NotNull
    private Integer chatroom;

    private Integer replyMessage;

}
