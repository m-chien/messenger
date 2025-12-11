package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.MessageDTO;
import com.example.WebCloneMessenger.DTO.MessageDetailProjection;
import com.example.WebCloneMessenger.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageResource {

    private final MessageService messageService;

    public MessageResource(final MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(messageService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createMessage(@RequestBody @Valid final MessageDTO messageDTO) {
        final Integer createdId = messageService.create(messageDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateMessage(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final MessageDTO messageDTO) {
        messageService.update(id, messageDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable(name = "id") final Integer id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chatroom/{chatRoomId}")
    public ResponseEntity<List<MessageDetailProjection>> getMessagesByChatRoom(@PathVariable(name = "chatRoomId") final Integer chatRoomId) {
        return ResponseEntity.ok(messageService.findByChatRoomId(chatRoomId));
    }
}
