package com.example.WebCloneMessenger.Model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChatRoomUserId implements Serializable {
    private Integer iduser;
    private Integer idchatroom;
}

