package com.example.WebCloneMessenger.Controller;


import com.example.WebCloneMessenger.DTO.AttachmentDTO;
import com.example.WebCloneMessenger.service.AttachmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttachmentResource {

    private final AttachmentService attachmentService;

    public AttachmentResource(final AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public ResponseEntity<List<AttachmentDTO>> getAllAttachments() {
        return ResponseEntity.ok(attachmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentDTO> getAttachment(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(attachmentService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createAttachment(
            @RequestBody @Valid final AttachmentDTO attachmentDTO) {
        final Integer createdId = attachmentService.create(attachmentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateAttachment(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final AttachmentDTO attachmentDTO) {
        attachmentService.update(id, attachmentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable(name = "id") final Integer id) {
        attachmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
