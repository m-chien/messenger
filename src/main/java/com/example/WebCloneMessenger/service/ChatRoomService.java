package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.ChatRoomDTO;
import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteChatRoom;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.ChatRoomMapper;
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
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher publisher;
    private final ChatRoomMapper chatRoomMapper;


    public List<ChatRoomDTO> findAll() {
        final List<ChatRoom> chatRooms = chatRoomRepository.findAll(Sort.by("id"));
        return chatRooms.stream().map(chatRoomMapper::toDTO)
                .toList();
    }

    public ChatRoomDTO get(final Integer id) {
        return chatRoomRepository.findById(id).map(chatRoomMapper::toDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ChatRoomDTO chatRoomDTO) {
        final ChatRoom chatRoom = chatRoomMapper.toEntity(chatRoomDTO);

        if (chatRoomDTO.getCreator() == null) {
            throw new IllegalArgumentException("Creator is required");
        }

        final User creator = userRepository.findById(chatRoomDTO.getCreator())
                .orElseThrow(() -> new NotFoundException("creator not found"));
        chatRoom.setCreator(creator);

        if (chatRoomDTO.getLastMessage() != null) {
            final Message lastMessage = messageRepository.findById(chatRoomDTO.getLastMessage())
                    .orElseThrow(() -> new NotFoundException("lastMessage not found"));
            chatRoom.setLastMessage(lastMessage);
        }

        return chatRoomRepository.save(chatRoom).getId();
    }

    public void update(final Integer id, final ChatRoomDTO chatRoomDTO) {
        final ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(chatRoomDTO, chatRoom);
        chatRoomRepository.save(chatRoom);
    }

    public void delete(final Integer id) {
        final ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteChatRoom(id));
        chatRoomRepository.delete(chatRoom);
    }

    private ChatRoomDTO mapToDTO(final ChatRoom chatRoom, final ChatRoomDTO chatRoomDTO) {
        chatRoomDTO.setId(chatRoom.getId());
        chatRoomDTO.setName(chatRoom.getName());
        chatRoomDTO.setLogo(chatRoom.getLogo());
        chatRoomDTO.setBackground(chatRoom.getBackground());
        chatRoomDTO.setIcon(chatRoom.getIcon());
        chatRoomDTO.setType(chatRoom.getType());
        chatRoomDTO.setCreator(chatRoom.getCreator() == null ? null : chatRoom.getCreator().getId());
        chatRoomDTO.setLastMessage(chatRoom.getLastMessage() == null ? null : chatRoom.getLastMessage().getId());
        return chatRoomDTO;
    }

    private ChatRoom mapToEntity(final ChatRoomDTO chatRoomDTO, final ChatRoom chatRoom) {
        chatRoom.setName(chatRoomDTO.getName());
        chatRoom.setLogo(chatRoomDTO.getLogo());
        chatRoom.setBackground(chatRoomDTO.getBackground());
        chatRoom.setIcon(chatRoomDTO.getIcon());
        chatRoom.setType(chatRoomDTO.getType());
        final User creator = chatRoomDTO.getCreator() == null ? null : userRepository.findById(chatRoomDTO.getCreator())
                .orElseThrow(() -> new NotFoundException("creator not found"));
        chatRoom.setCreator(creator);
        final Message lastMessage = chatRoomDTO.getLastMessage() == null ? null : messageRepository.findById(chatRoomDTO.getLastMessage())
                .orElseThrow(() -> new NotFoundException("lastMessage not found"));
        chatRoom.setLastMessage(lastMessage);
        return chatRoom;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChatRoom creatorChatRoom = chatRoomRepository.findFirstByCreatorId(event.getId());
        if (creatorChatRoom != null) {
            referencedException.setKey("user.chatRoom.creator.referenced");
            referencedException.addParam(creatorChatRoom.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteMessage.class)
    public void on(final BeforeDeleteMessage event) {
        final ReferencedException referencedException = new ReferencedException();
        final ChatRoom lastMessageChatRoom = chatRoomRepository.findFirstByLastMessageId(event.getId());
        if (lastMessageChatRoom != null) {
            referencedException.setKey("message.chatRoom.lastMessage.referenced");
            referencedException.addParam(lastMessageChatRoom.getId());
            throw referencedException;
        }
    }

}
