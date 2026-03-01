package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.*;
import com.example.WebCloneMessenger.Model.Attachment;
import com.example.WebCloneMessenger.Model.ChatRoom;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteChatRoom;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.AttachmentMapper;
import com.example.WebCloneMessenger.mapper.MessageMapper;
import com.example.WebCloneMessenger.mapper.UserMapper;
import com.example.WebCloneMessenger.repos.*;
import com.example.WebCloneMessenger.Exception.AppException;
import com.example.WebCloneMessenger.Exception.ErrorCode;
import com.example.WebCloneMessenger.Exception.ReferencedException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher publisher;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final FileUploadService minioService;


    public List<MessageDTO> findAll() {
        final List<Message> messages = messageRepository.findAll(Sort.by("id"));
        return messages.stream().map(messageMapper::toDto)
                .toList();
    }

    public MessageDTO get(final Integer id) {
        return messageRepository.findById(id).map(messageMapper::toDto)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
    }


    public MessageResponseDTO create(final MessageDTO messageDTO) {
        if (messageDTO.getUserId() == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (messageDTO.getChatroom() == null) {
            throw new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getChatroom())
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        Message message = messageMapper.toEntity(messageDTO);
        message.setIdUser(user);
        message.setChatroom(chatRoom);
        message.setDateSend(LocalDateTime.now());
        message.setIsPin(false);

        if (messageDTO.getReplyMessage() != null) {
            Message replyMsg = messageRepository.findById(messageDTO.getReplyMessage())
                    .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
            message.setReplyMessage(replyMsg);
        }

        Message savedMessage = messageRepository.save(message);
        messageRepository.flush();

        List<AttachmentDTO> attachmentDTOs = new ArrayList<>();
        if (messageDTO.getAttachments() != null) {
            for (AttachmentDTO a : messageDTO.getAttachments()) {
                Attachment attachment = attachmentMapper.toEntity(a);
                attachment.setId(null);
                attachment.setIdmessage(savedMessage);
                Attachment savedAttachment = attachmentRepository.save(attachment);
                AttachmentDTO attachmentDTO = attachmentMapper.toDto(savedAttachment);
                attachmentDTO.setFileUrl(minioService.getPresignedUrl(attachment.getFileUrl()));
                attachmentDTOs.add(attachmentDTO);
            }
        }

        return MessageResponseDTO.builder()
                .id(savedMessage.getId())
                .type(savedMessage.getType())
                .content(savedMessage.getContent())
                .isPin(savedMessage.getIsPin())
                .dateSend(savedMessage.getDateSend())
                .userId(user.getId())
                .isOnline(user.getIsOnline())
                .userName(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .attachments(attachmentDTOs)
                .build();
    }


    public void update(final Integer id, final MessageDTO messageDTO) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        Message updated = messageMapper.toEntity(messageDTO);

        message.setType(updated.getType());
        message.setDateSend(updated.getDateSend());
        message.setContent(updated.getContent());
        message.setIsPin(updated.getIsPin());

        if (messageDTO.getUserId() != null) {
            User user = userRepository.findById(messageDTO.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            message.setIdUser(user);
        }

        if (messageDTO.getChatroom() != null) {
            ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getChatroom())
                    .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));
            message.setChatroom(chatRoom);
        }

        if (messageDTO.getReplyMessage() != null) {
            Message replyMsg = messageRepository.findById(messageDTO.getReplyMessage())
                    .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
            message.setReplyMessage(replyMsg);
        }
        message.setType("text");
        messageRepository.save(message);
    }

    public void delete(final Integer id) {
        final Message message = messageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
        publisher.publishEvent(new BeforeDeleteMessage(id));
        messageRepository.delete(message);
    }

    public List<MessageResponseDTO> findByChatRoomId(final Integer chatRoomId) {

        List<MessageDetailProjection> messages =
                messageRepository.findMessageDetailsByChatRoomId(chatRoomId);

        List<Integer> messageIds = messages.stream()
                .map(MessageDetailProjection::getId)
                .toList();

        List<Attachment> attachments =
                attachmentRepository.findByIdmessage_IdIn(messageIds);

        Map<Integer, List<AttachmentDTO>> attachmentMap =
                attachments.stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getIdmessage().getId(),
                                Collectors.mapping(a -> {
                                    AttachmentDTO dto = attachmentMapper.toDto(a);
                                    dto.setFileUrl(
                                            minioService.getPresignedUrl(a.getFileUrl())
                                    );
                                    return dto;
                                }, Collectors.toList())
                        ));

        // 🔥 GHÉP MESSAGE + ATTACHMENT
        return messages.stream().map(m -> {
            MessageResponseDTO dto = new MessageResponseDTO();

            dto.setId(m.getId());
            dto.setType(m.getType());
            dto.setContent(m.getContent());
            dto.setIsPin(m.getIsPin());
            dto.setDateSend(m.getDateSend());

            dto.setUserId(m.getUserId());
            dto.setIsOnline(m.getIsOnline());
            dto.setUserName(m.getUserName());
            dto.setAvatarUrl(m.getAvatarUrl());

            dto.setAttachments(
                    attachmentMap.getOrDefault(m.getId(), List.of())
            );

            return dto;
        }).toList();
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

    public MessageDetailProjection findMessageDetailById(int idNewMessage) {
        return messageRepository.findMessageDetailById(idNewMessage);
    }

    public void notifySidebarUsers(Integer roomId, MessageResponseDTO msg) {
        // Lấy tất cả userId trong phòng (bao gồm người gửi). Bạn có thể bỏ người gửi nếu muốn.
        List<Integer> userIds = chatRoomUserRepository.findUserIdsByChatroom(roomId);

        // Tạo DTO nhẹ cho sidebar (cần map theo DTO bên FE)
        SidebarMessageDTO dto = new SidebarMessageDTO(
                roomId,
                msg.getContent(),
                msg.getDateSend(),
                msg.getUserId()
        );

        // Gửi cho từng user
        for (Integer userId : userIds) {
            simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/sidebar", dto);
        }
    }
}
