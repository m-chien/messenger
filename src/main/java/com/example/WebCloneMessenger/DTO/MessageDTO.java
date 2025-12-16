package com.example.WebCloneMessenger.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Integer id;

    @Size(max = 50)
    private String type;

    private LocalDateTime dateSend;

    private String content;

    @JsonProperty("isPin")
    private Boolean isPin;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer chatroom;

    private Integer replyMessage;

}
