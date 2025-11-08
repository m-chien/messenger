package com.example.WebCloneMessenger.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class BlockListDTO {

    private Integer id;

    private OffsetDateTime blockedDate;

    @NotNull
    private Integer blocker;

    @NotNull
    private Integer blocked;

}
