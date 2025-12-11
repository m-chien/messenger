package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.ChatRoomUserDTO;
import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.ChatRoomUser;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteChatRoom;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.repos.ChatRoomRepository;
import com.example.WebCloneMessenger.repos.ChatRoomUserRepository;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
import com.example.WebCloneMessenger.Exception.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChatRoomUserService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public ChatRoomUserService(final ChatRoomUserRepository chatRoomUserRepository,
            final UserRepository userRepository, final ChatRoomRepository chatRoomRepository,
            final MessageRepository messageRepository) {
        this.chatRoomUserRepository = chatRoomUserRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
    }

    public List<ChatRoomUserDTO> findAll() {
        final List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAll(Sort.by("id"));
        return chatRoomUsers.stream()
                .map(chatRoomUser -> mapToDTO(chatRoomUser, new ChatRoomUserDTO()))
                .toList();
    }

    public ChatRoomUserDTO get(final Long id) {
        return chatRoomUserRepository.findById(id)
                .map(chatRoomUser -> mapToDTO(chatRoomUser, new ChatRoomUserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ChatRoomUserDTO chatRoomUserDTO) {
        final ChatRoomUser chatRoomUser = new ChatRoomUser();
        mapToEntity(chatRoomUserDTO, chatRoomUser);
        return chatRoomUserRepository.save(chatRoomUser).getId();
    }

    public void update(final Long id, final ChatRoomUserDTO chatRoomUserDTO) {
        final ChatRoomUser chatRoomUser = chatRoomUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(chatRoomUserDTO, chatRoomUser);
        chatRoomUserRepository.save(chatRoomUser);
    }

    public void delete(final Long id) {
        final ChatRoomUser chatRoomUser = chatRoomUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        chatRoomUserRepository.delete(chatRoomUser);
    }

    private ChatRoomUserDTO mapToDTO(final ChatRoomUser chatRoomUser,
            final ChatRoomUserDTO chatRoomUserDTO) {
        chatRoomUserDTO.setId(chatRoomUser.getId());
        chatRoomUserDTO.setIduser(chatRoomUser.getIduser() == null ? null : chatRoomUser.getIduser().getId());
        chatRoomUserDTO.setIdchatroom(chatRoomUser.getIdchatroom() == null ? null : chatRoomUser.getIdchatroom().getId());
        chatRoomUserDTO.setLastSeenMessage(chatRoomUser.getLastSeenMessage() == null ? null : chatRoomUser.getLastSeenMessage().getId());
        return chatRoomUserDTO;
    }

    private ChatRoomUser mapToEntity(final ChatRoomUserDTO chatRoomUserDTO,
            final ChatRoomUser chatRoomUser) {
        final User iduser = chatRoomUserDTO.getIduser() == null ? null : userRepository.findById(chatRoomUserDTO.getIduser())
                .orElseThrow(() -> new NotFoundException("iduser not found"));
        chatRoomUser.setIduser(iduser);
        final ChatRoom idchatroom = chatRoomUserDTO.getIdchatroom() == null ? null : chatRoomRepository.findById(chatRoomUserDTO.getIdchatroom())
                .orElseThrow(() -> new NotFoundException("idchatroom not found"));
        chatRoomUser.setIdchatroom(idchatroom);
        final Message lastSeenMessage = chatRoomUserDTO.getLastSeenMessage() == null ? null : messageRepository.findById(chatRoomUserDTO.getLastSeenMessage())
                .orElseThrow(() -> new NotFoundException("lastSeenMessage not found"));
        chatRoomUser.setLastSeenMessage(lastSeenMessage);
        return chatRoomUser;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChatRoomUser iduserChatRoomUser = chatRoomUserRepository.findFirstByIduserId(event.getId());
        if (iduserChatRoomUser != null) {
            referencedException.setKey("user.chatRoomUser.iduser.referenced");
            referencedException.addParam(iduserChatRoomUser.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteChatRoom.class)
    public void on(final BeforeDeleteChatRoom event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChatRoomUser idchatroomChatRoomUser = chatRoomUserRepository.findFirstByIdchatroomId(event.getId());
        if (idchatroomChatRoomUser != null) {
            referencedException.setKey("chatRoom.chatRoomUser.idchatroom.referenced");
            referencedException.addParam(idchatroomChatRoomUser.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMessage.class)
    public void on(final BeforeDeleteMessage event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChatRoomUser lastSeenMessageChatRoomUser = chatRoomUserRepository.findFirstByLastSeenMessageId(event.getId());
        if (lastSeenMessageChatRoomUser != null) {
            referencedException.setKey("message.chatRoomUser.lastSeenMessage.referenced");
            referencedException.addParam(lastSeenMessageChatRoomUser.getId());
            throw referencedException;
        }
    }

}
