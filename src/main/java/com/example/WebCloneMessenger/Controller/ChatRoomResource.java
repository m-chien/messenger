package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.ChatRoomDTO;
import com.example.WebCloneMessenger.service.ChatRoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/chatRooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomResource {

    private final ChatRoomService chatRoomService;

    public ChatRoomResource(final ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRooms() {
        return ResponseEntity.ok(chatRoomService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(chatRoomService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createChatRoom(
            @RequestBody @Valid final ChatRoomDTO chatRoomDTO) {
        final Integer createdId = chatRoomService.create(chatRoomDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateChatRoom(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final ChatRoomDTO chatRoomDTO) {
        chatRoomService.update(id, chatRoomDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable(name = "id") final Integer id) {
        chatRoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
