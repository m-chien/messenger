package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.ReactionDTO;
import com.example.WebCloneMessenger.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/reactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReactionResource {

    private final ReactionService reactionService;

    public ReactionResource(final ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @GetMapping
    public ResponseEntity<List<ReactionDTO>> getAllReactions() {
        return ResponseEntity.ok(reactionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionDTO> getReaction(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(reactionService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createReaction(
            @RequestBody @Valid final ReactionDTO reactionDTO) {
        final Integer createdId = reactionService.create(reactionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateReaction(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final ReactionDTO reactionDTO) {
        reactionService.update(id, reactionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable(name = "id") final Integer id) {
        reactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
