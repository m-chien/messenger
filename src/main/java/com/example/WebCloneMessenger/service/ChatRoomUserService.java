package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.ChatRoomUserDTO;
import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.ChatRoomUser;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteChatRoom;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.ChatRoomUserMapper;
import com.example.WebCloneMessenger.repos.ChatRoomRepository;
import com.example.WebCloneMessenger.repos.ChatRoomUserRepository;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
import com.example.WebCloneMessenger.Exception.ReferencedException;
import jakarta.transaction.Transactional;
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
    private final ChatRoomUserMapper chatRoomUserMapper;

    public ChatRoomUserService(final ChatRoomUserRepository chatRoomUserRepository,
            final UserRepository userRepository, final ChatRoomRepository chatRoomRepository,
            final MessageRepository messageRepository, final ChatRoomUserMapper chatRoomUserMapper) {
        this.chatRoomUserRepository = chatRoomUserRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.chatRoomUserMapper = chatRoomUserMapper;
    }

    public List<ChatRoomUserDTO> findAll() {
        final List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAll(Sort.by("id"));
        return chatRoomUsers.stream()
                .map(chatRoomUserMapper::toDto)
                .toList();
    }

    public ChatRoomUserDTO get(final Long id) {
        return chatRoomUserRepository.findById(id)
                .map(chatRoomUserMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public void create(final ChatRoomUserDTO chatRoomUserDTO) {
        final ChatRoomUser chatRoomUser = chatRoomUserMapper.toEntity(chatRoomUserDTO);
        mapReq(chatRoomUserDTO, chatRoomUser);
        chatRoomUserRepository.save(chatRoomUser);
    }

    public void update(final Long id, final ChatRoomUserDTO chatRoomUserDTO) {
        final ChatRoomUser chatRoomUser = chatRoomUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        chatRoomUserMapper.toEntity(chatRoomUserDTO);
        mapReq(chatRoomUserDTO, chatRoomUser);
        chatRoomUserRepository.save(chatRoomUser);
    }

    public void delete(final Long id) {
        final ChatRoomUser chatRoomUser = chatRoomUserRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        chatRoomUserRepository.delete(chatRoomUser);
    }

    private void mapReq(final ChatRoomUserDTO chatRoomUserDTO,
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
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        if (chatRoomUserRepository.existsByIduser_Id(event.getId())) {
            ReferencedException ex = new ReferencedException();
            ex.setKey("user.chatRoomUser.iduser.referenced");
            throw ex;
        }
    }


    @EventListener(BeforeDeleteChatRoom.class)
    public void on(final BeforeDeleteChatRoom event) {
        if (chatRoomUserRepository.existsByIdchatroom_Id(event.getId())) {
            ReferencedException ex = new ReferencedException();
            ex.setKey("chatRoom.chatRoomUser.idchatroom.referenced");
            throw ex;
        }
    }


    @EventListener(BeforeDeleteMessage.class)
    public void on(final BeforeDeleteMessage event) {
        if (chatRoomUserRepository.existsByLastSeenMessage_Id(event.getId())) {
            ReferencedException ex = new ReferencedException();
            ex.setKey("message.chatRoomUser.lastSeenMessage.referenced");
            throw ex;
        }
    }


    @Transactional
    public void readLatestMessage(int chatRoomId, int userId) {

        ChatRoomUser chatRoomUser =
                chatRoomUserRepository
                        .findByIdchatroom_IdAndIduser_Id(chatRoomId, userId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("chatroom not found"));

        Message lastMessage = chatRoom.getLastMessage();
        System.out.println("Last message ID: " + (lastMessage != null ? lastMessage.getId() : "null"));

        // Chatroom chưa có tin nhắn → không cần update
        if (lastMessage == null) return;

        // Chỉ update nếu message mới hơn
        if (
                chatRoomUser.getLastSeenMessage() == null ||
                        chatRoomUser.getLastSeenMessage().getId() < lastMessage.getId()
        ) {
            chatRoomUser.setLastSeenMessage(lastMessage);
            chatRoomUserRepository.save(chatRoomUser);
        }
    }
}
