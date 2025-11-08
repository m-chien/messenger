package com.example.WebCloneMessenger.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatRoomDTO {

    private Integer id;

    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String logo;

    @Size(max = 500)
    private String background;

    @Size(max = 500)
    private String icon;

    @Size(max = 50)
    private String type;

    @NotNull
    private Integer creator;

    private Integer lastMessage;

}
