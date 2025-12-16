package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.ReactionDTO;
import com.example.WebCloneMessenger.Model.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(target = "iduser", expression = "java(reaction.getIduser() != null ? reaction.getIduser().getId() : null)")
    @Mapping(target = "message", expression = "java(reaction.getMessage() != null ? reaction.getMessage().getId() : null)")
    ReactionDTO toDto(Reaction reaction);

    @Mapping(target = "iduser", ignore = true)
    @Mapping(target = "message", ignore = true)
    Reaction toEntity(ReactionDTO dto);
}
