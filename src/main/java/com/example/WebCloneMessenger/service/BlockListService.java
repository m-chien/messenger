package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.BlockListDTO;
import com.example.WebCloneMessenger.Model.BlockList;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.repos.BlockListRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.util.NotFoundException;
import com.example.WebCloneMessenger.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BlockListService {

    private final BlockListRepository blockListRepository;
    private final UserRepository userRepository;

    public BlockListService(final BlockListRepository blockListRepository,
            final UserRepository userRepository) {
        this.blockListRepository = blockListRepository;
        this.userRepository = userRepository;
    }

    public List<BlockListDTO> findAll() {
        final List<BlockList> blockLists = blockListRepository.findAll(Sort.by("id"));
        return blockLists.stream()
                .map(blockList -> mapToDTO(blockList, new BlockListDTO()))
                .toList();
    }

    public BlockListDTO get(final Integer id) {
        return blockListRepository.findById(id)
                .map(blockList -> mapToDTO(blockList, new BlockListDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final BlockListDTO blockListDTO) {
        final BlockList blockList = new BlockList();
        mapToEntity(blockListDTO, blockList);
        return blockListRepository.save(blockList).getId();
    }

    public void update(final Integer id, final BlockListDTO blockListDTO) {
        final BlockList blockList = blockListRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(blockListDTO, blockList);
        blockListRepository.save(blockList);
    }

    public void delete(final Integer id) {
        final BlockList blockList = blockListRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        blockListRepository.delete(blockList);
    }

    private BlockListDTO mapToDTO(final BlockList blockList, final BlockListDTO blockListDTO) {
        blockListDTO.setId(blockList.getId());
        blockListDTO.setBlockedDate(blockList.getBlockedDate());
        blockListDTO.setBlocker(blockList.getBlocker() == null ? null : blockList.getBlocker().getId());
        blockListDTO.setBlocked(blockList.getBlocked() == null ? null : blockList.getBlocked().getId());
        return blockListDTO;
    }

    private BlockList mapToEntity(final BlockListDTO blockListDTO, final BlockList blockList) {
        blockList.setBlockedDate(blockListDTO.getBlockedDate());
        final User blocker = blockListDTO.getBlocker() == null ? null : userRepository.findById(blockListDTO.getBlocker())
                .orElseThrow(() -> new NotFoundException("blocker not found"));
        blockList.setBlocker(blocker);
        final User blocked = blockListDTO.getBlocked() == null ? null : userRepository.findById(blockListDTO.getBlocked())
                .orElseThrow(() -> new NotFoundException("blocked not found"));
        blockList.setBlocked(blocked);
        return blockList;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final BlockList blockerBlockList = blockListRepository.findFirstByBlockerId(event.getId());
        if (blockerBlockList != null) {
            referencedException.setKey("user.blockList.blocker.referenced");
            referencedException.addParam(blockerBlockList.getId());
            throw referencedException;
        }
        final BlockList blockedBlockList = blockListRepository.findFirstByBlockedId(event.getId());
        if (blockedBlockList != null) {
            referencedException.setKey("user.blockList.blocked.referenced");
            referencedException.addParam(blockedBlockList.getId());
            throw referencedException;
        }
    }

}
