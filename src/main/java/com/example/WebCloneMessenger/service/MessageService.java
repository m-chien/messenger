package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.MessageDTO;
import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteChatRoom;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.MessageMapper;
import com.example.WebCloneMessenger.repos.ChatRoomRepository;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.util.NotFoundException;
import com.example.WebCloneMessenger.util.ReferencedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher publisher;
    private final MessageMapper messageMapper;


    public List<MessageDTO> findAll() {
        final List<Message> messages = messageRepository.findAll(Sort.by("id"));
        return messages.stream().map(messageMapper::toDto)
                .toList();
    }

    public MessageDTO get(final Integer id) {
        return messageRepository.findById(id).map(messageMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MessageDTO messageDTO) {
        Message message = messageMapper.toEntity(messageDTO);

        if (messageDTO.getIdUser() == null) {
            throw new IllegalArgumentException("User is required");
        }
        User user = userRepository.findById(messageDTO.getIdUser())
                .orElseThrow(() -> new NotFoundException("User not found"));
        message.setIdUser(user);

        if (messageDTO.getChatroom() == null) {
            throw new IllegalArgumentException("Chatroom is required");
        }
        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getChatroom())
                .orElseThrow(() -> new NotFoundException("Chatroom not found"));
        message.setChatroom(chatRoom);

        if (messageDTO.getReplyMessage() != null) {
            Message replyMsg = messageRepository.findById(messageDTO.getReplyMessage())
                    .orElseThrow(() -> new NotFoundException("replyMessage not found"));
            message.setReplyMessage(replyMsg);
        }

        return messageRepository.save(message).getId();
    }


    public void update(final Integer id, final MessageDTO messageDTO) {
        Message message = messageRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        Message updated = messageMapper.toEntity(messageDTO);

        message.setType(updated.getType());
        message.setDateSend(updated.getDateSend());
        message.setContent(updated.getContent());
        message.setIsPin(updated.getIsPin());

        if (messageDTO.getIdUser() != null) {
            User user = userRepository.findById(messageDTO.getIdUser())
                    .orElseThrow(() -> new NotFoundException("iduser not found"));
            message.setIdUser(user);
        }

        if (messageDTO.getChatroom() != null) {
            ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getChatroom())
                    .orElseThrow(() -> new NotFoundException("chatroom not found"));
            message.setChatroom(chatRoom);
        }

        if (messageDTO.getReplyMessage() != null) {
            Message replyMsg = messageRepository.findById(messageDTO.getReplyMessage())
                    .orElseThrow(() -> new NotFoundException("replyMessage not found"));
            message.setReplyMessage(replyMsg);
        }

        messageRepository.save(message);
    }

    public void delete(final Integer id) {
        final Message message = messageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteMessage(id));
        messageRepository.delete(message);
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Message iduserMessage = messageRepository.findFirstByIdUserId(event.getId());
        if (iduserMessage != null) {
            referencedException.setKey("user.message.iduser.referenced");
            referencedException.addParam(iduserMessage.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteChatRoom.class)
    public void on(final BeforeDeleteChatRoom event) {
        final ReferencedException referencedException = new ReferencedException();
        final Message chatroomMessage = messageRepository.findFirstByChatroomId(event.getId());
        if (chatroomMessage != null) {
            referencedException.setKey("chatRoom.message.chatroom.referenced");
            referencedException.addParam(chatroomMessage.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMessage.class)
    public void on(final BeforeDeleteMessage event) {
        final ReferencedException referencedException = new ReferencedException();
        final Message replyMessageMessage = messageRepository.findFirstByReplyMessageIdAndIdNot(event.getId(), event.getId());
        if (replyMessageMessage != null) {
            referencedException.setKey("message.message.replyMessage.referenced");
            referencedException.addParam(replyMessageMessage.getId());
            throw referencedException;
        }
    }

}
