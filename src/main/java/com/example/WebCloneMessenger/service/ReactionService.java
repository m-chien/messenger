package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.ReactionDTO;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.Reaction;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.ReactionMapper;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.repos.ReactionRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
import com.example.WebCloneMessenger.Exception.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReactionMapper reactionMapper;

    public ReactionService(final ReactionRepository reactionRepository,
            final UserRepository userRepository, final MessageRepository messageRepository,
            final ReactionMapper reactionMapper) {
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.reactionMapper = reactionMapper;
    }

    public List<ReactionDTO> findAll() {
        final List<Reaction> reactions = reactionRepository.findAll(Sort.by("id"));
        return reactions.stream()
                .map(reactionMapper::toDto)
                .toList();
    }

    public ReactionDTO get(final Integer id) {
        return reactionRepository.findById(id)
                .map(reactionMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ReactionDTO reactionDTO) {
        final Reaction reaction = reactionMapper.toEntity(reactionDTO);
        mapReq(reactionDTO, reaction);
        return reactionRepository.save(reaction).getId();
    }

    public void update(final Integer id, final ReactionDTO reactionDTO) {
        final Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        reactionMapper.toEntity(reactionDTO);
        mapReq(reactionDTO, reaction);
        reactionRepository.save(reaction);
    }

    public void delete(final Integer id) {
        final Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        reactionRepository.delete(reaction);
    }

    private void mapReq(final ReactionDTO reactionDTO, final Reaction reaction) {
        final User iduser = reactionDTO.getIduser() == null ? null : userRepository.findById(reactionDTO.getIduser())
                .orElseThrow(() -> new NotFoundException("iduser not found"));
        reaction.setIduser(iduser);
        final Message message = reactionDTO.getMessage() == null ? null : messageRepository.findById(reactionDTO.getMessage())
                .orElseThrow(() -> new NotFoundException("message not found"));
        reaction.setMessage(message);
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Reaction iduserReaction = reactionRepository.findFirstByIduserId(event.getId());
        if (iduserReaction != null) {
            referencedException.setKey("user.reaction.iduser.referenced");
            referencedException.addParam(iduserReaction.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMessage.class)
    public void on(final BeforeDeleteMessage event) {
        final ReferencedException referencedException = new ReferencedException();
        final Reaction messageReaction = reactionRepository.findFirstByMessageId(event.getId());
        if (messageReaction != null) {
            referencedException.setKey("message.reaction.message.referenced");
            referencedException.addParam(messageReaction.getId());
            throw referencedException;
        }
    }

}
