package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.ChatRoomUserDTO;
import com.example.WebCloneMessenger.service.ChatRoomUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/chatRoomUsers", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomUserResource {

    private final ChatRoomUserService chatRoomUserService;

    public ChatRoomUserResource(final ChatRoomUserService chatRoomUserService) {
        this.chatRoomUserService = chatRoomUserService;
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomUserDTO>> getAllChatRoomUsers() {
        return ResponseEntity.ok(chatRoomUserService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomUserDTO> getChatRoomUser(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(chatRoomUserService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createChatRoomUser(
            @RequestBody @Valid final ChatRoomUserDTO chatRoomUserDTO) {
        final Integer createdId = chatRoomUserService.create(chatRoomUserDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChatRoomUser(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChatRoomUserDTO chatRoomUserDTO) {
        chatRoomUserService.update(id, chatRoomUserDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatRoomUser(@PathVariable(name = "id") final Long id) {
        chatRoomUserService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
