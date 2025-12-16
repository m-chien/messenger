package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.AttachmentDTO;
import com.example.WebCloneMessenger.Model.Attachment;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.mapper.AttachmentMapper;
import com.example.WebCloneMessenger.repos.AttachmentRepository;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
import com.example.WebCloneMessenger.Exception.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;
    private final AttachmentMapper attachmentMapper;

    public AttachmentService(final AttachmentRepository attachmentRepository,
            final MessageRepository messageRepository, final AttachmentMapper attachmentMapper) {
        this.attachmentRepository = attachmentRepository;
        this.messageRepository = messageRepository;
        this.attachmentMapper = attachmentMapper;
    }

    public List<AttachmentDTO> findAll() {
        final List<Attachment> attachments = attachmentRepository.findAll(Sort.by("id"));
        return attachments.stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    public AttachmentDTO get(final Integer id) {
        return attachmentRepository.findById(id)
                .map(attachmentMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AttachmentDTO attachmentDTO) {
        final Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        mapReq(attachmentDTO, attachment);
        return attachmentRepository.save(attachment).getId();
    }

    public void update(final Integer id, final AttachmentDTO attachmentDTO) {
        final Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        attachmentMapper.toEntity(attachmentDTO);
        mapReq(attachmentDTO, attachment);
        attachmentRepository.save(attachment);
    }

    public void delete(final Integer id) {
        final Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        attachmentRepository.delete(attachment);
    }

    private void mapReq(final AttachmentDTO attachmentDTO, final Attachment attachment) {
        final Message idmessage = attachmentDTO.getIdmessage() == null ? null : messageRepository.findById(attachmentDTO.getIdmessage())
                .orElseThrow(() -> new NotFoundException("idmessage not found"));
        attachment.setIdmessage(idmessage);
    }

    @EventListener(BeforeDeleteMessage.class)
    public void on(final BeforeDeleteMessage event) {
        final ReferencedException referencedException = new ReferencedException();
        final Attachment idmessageAttachment = attachmentRepository.findFirstByIdmessageId(event.getId());
        if (idmessageAttachment != null) {
            referencedException.setKey("message.attachment.idmessage.referenced");
            referencedException.addParam(idmessageAttachment.getId());
            throw referencedException;
        }
    }

}
