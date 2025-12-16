package com.example.WebCloneMessenger.mapper;

import com.example.WebCloneMessenger.DTO.BlockListDTO;
import com.example.WebCloneMessenger.Model.BlockList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlockListMapper {

    @Mapping(target = "blocker", expression = "java(blockList.getBlocker() != null ? blockList.getBlocker().getId() : null)")
    @Mapping(target = "blocked", expression = "java(blockList.getBlocked() != null ? blockList.getBlocked().getId() : null)")
    BlockListDTO toDto(BlockList blockList);

    @Mapping(target = "blocker", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    BlockList toEntity(BlockListDTO dto);
}
