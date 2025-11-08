package com.example.WebCloneMessenger.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ReactionDTO {

    private Integer id;

    @Size(max = 50)
    private String type;

    private OffsetDateTime dateSend;

    @NotNull
    private Integer iduser;

    @NotNull
    private Integer message;

}
