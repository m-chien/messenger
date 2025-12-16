package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.AttachmentDTO;
import com.example.WebCloneMessenger.Model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(target = "idmessage", expression = "java(attachment.getIdmessage() != null ? attachment.getIdmessage().getId() : null)")
    AttachmentDTO toDto(Attachment attachment);

    @Mapping(target = "idmessage", ignore = true)
    Attachment toEntity(AttachmentDTO dto);
}
