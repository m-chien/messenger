package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.FriendRequestDTO;
import com.example.WebCloneMessenger.service.FriendRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/friendRequests", produces = MediaType.APPLICATION_JSON_VALUE)
public class FriendRequestResource {

    private final FriendRequestService friendRequestService;

    public FriendRequestResource(final FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @GetMapping
    public ResponseEntity<List<FriendRequestDTO>> getAllFriendRequests() {
        return ResponseEntity.ok(friendRequestService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendRequestDTO> getFriendRequest(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(friendRequestService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createFriendRequest(
            @RequestBody @Valid final FriendRequestDTO friendRequestDTO) {
        final Integer createdId = friendRequestService.create(friendRequestDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateFriendRequest(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final FriendRequestDTO friendRequestDTO) {
        friendRequestService.update(id, friendRequestDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendRequest(@PathVariable(name = "id") final Integer id) {
        friendRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
