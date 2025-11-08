package com.example.WebCloneMessenger.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AttachmentDTO {

    private Integer id;

    @Size(max = 500)
    private String fileUrl;

    @Size(max = 50)
    private String fileType;

    @Size(max = 255)
    private String fileName;

    private Long fileSize;

    @NotNull
    private Integer idmessage;

}
