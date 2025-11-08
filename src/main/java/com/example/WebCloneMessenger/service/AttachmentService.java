package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.AttachmentDTO;
import com.example.WebCloneMessenger.Model.Attachment;
import com.example.WebCloneMessenger.Model.Message;
import com.example.WebCloneMessenger.events.BeforeDeleteMessage;
import com.example.WebCloneMessenger.repos.AttachmentRepository;
import com.example.WebCloneMessenger.repos.MessageRepository;
import com.example.WebCloneMessenger.util.NotFoundException;
import com.example.WebCloneMessenger.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;

    public AttachmentService(final AttachmentRepository attachmentRepository,
            final MessageRepository messageRepository) {
        this.attachmentRepository = attachmentRepository;
        this.messageRepository = messageRepository;
    }

    public List<AttachmentDTO> findAll() {
        final List<Attachment> attachments = attachmentRepository.findAll(Sort.by("id"));
        return attachments.stream()
                .map(attachment -> mapToDTO(attachment, new AttachmentDTO()))
                .toList();
    }

    public AttachmentDTO get(final Integer id) {
        return attachmentRepository.findById(id)
                .map(attachment -> mapToDTO(attachment, new AttachmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AttachmentDTO attachmentDTO) {
        final Attachment attachment = new Attachment();
        mapToEntity(attachmentDTO, attachment);
        return attachmentRepository.save(attachment).getId();
    }

    public void update(final Integer id, final AttachmentDTO attachmentDTO) {
        final Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(attachmentDTO, attachment);
        attachmentRepository.save(attachment);
    }

    public void delete(final Integer id) {
        final Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        attachmentRepository.delete(attachment);
    }

    private AttachmentDTO mapToDTO(final Attachment attachment, final AttachmentDTO attachmentDTO) {
        attachmentDTO.setId(attachment.getId());
        attachmentDTO.setFileUrl(attachment.getFileUrl());
        attachmentDTO.setFileType(attachment.getFileType());
        attachmentDTO.setFileName(attachment.getFileName());
        attachmentDTO.setFileSize(attachment.getFileSize());
        attachmentDTO.setIdmessage(attachment.getIdmessage() == null ? null : attachment.getIdmessage().getId());
        return attachmentDTO;
    }

    private Attachment mapToEntity(final AttachmentDTO attachmentDTO, final Attachment attachment) {
        attachment.setFileUrl(attachmentDTO.getFileUrl());
        attachment.setFileType(attachmentDTO.getFileType());
        attachment.setFileName(attachmentDTO.getFileName());
        attachment.setFileSize(attachmentDTO.getFileSize());
        final Message idmessage = attachmentDTO.getIdmessage() == null ? null : messageRepository.findById(attachmentDTO.getIdmessage())
                .orElseThrow(() -> new NotFoundException("idmessage not found"));
        attachment.setIdmessage(idmessage);
        return attachment;
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
