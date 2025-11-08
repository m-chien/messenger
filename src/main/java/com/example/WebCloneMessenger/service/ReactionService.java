package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.ReactionDTO;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.Reaction;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.repos.ReactionRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.util.NotFoundException;
import com.example.WebCloneMessenger.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ReactionService(final ReactionRepository reactionRepository,
            final UserRepository userRepository, final MessageRepository messageRepository) {
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public List<ReactionDTO> findAll() {
        final List<Reaction> reactions = reactionRepository.findAll(Sort.by("id"));
        return reactions.stream()
                .map(reaction -> mapToDTO(reaction, new ReactionDTO()))
                .toList();
    }

    public ReactionDTO get(final Integer id) {
        return reactionRepository.findById(id)
                .map(reaction -> mapToDTO(reaction, new ReactionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ReactionDTO reactionDTO) {
        final Reaction reaction = new Reaction();
        mapToEntity(reactionDTO, reaction);
        return reactionRepository.save(reaction).getId();
    }

    public void update(final Integer id, final ReactionDTO reactionDTO) {
        final Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(reactionDTO, reaction);
        reactionRepository.save(reaction);
    }

    public void delete(final Integer id) {
        final Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        reactionRepository.delete(reaction);
    }

    private ReactionDTO mapToDTO(final Reaction reaction, final ReactionDTO reactionDTO) {
        reactionDTO.setId(reaction.getId());
        reactionDTO.setType(reaction.getType());
        reactionDTO.setDateSend(reaction.getDateSend());
        reactionDTO.setIduser(reaction.getIduser() == null ? null : reaction.getIduser().getId());
        reactionDTO.setMessage(reaction.getMessage() == null ? null : reaction.getMessage().getId());
        return reactionDTO;
    }

    private Reaction mapToEntity(final ReactionDTO reactionDTO, final Reaction reaction) {
        reaction.setType(reactionDTO.getType());
        reaction.setDateSend(reactionDTO.getDateSend());
        final User iduser = reactionDTO.getIduser() == null ? null : userRepository.findById(reactionDTO.getIduser())
                .orElseThrow(() -> new NotFoundException("iduser not found"));
        reaction.setIduser(iduser);
        final Message message = reactionDTO.getMessage() == null ? null : messageRepository.findById(reactionDTO.getMessage())
                .orElseThrow(() -> new NotFoundException("message not found"));
        reaction.setMessage(message);
        return reaction;
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
