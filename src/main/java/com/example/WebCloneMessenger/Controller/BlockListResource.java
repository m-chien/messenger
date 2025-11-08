package com.example.WebCloneMessenger.Controller;


import com.example.WebCloneMessenger.DTO.BlockListDTO;
import com.example.WebCloneMessenger.service.BlockListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/blockLists", produces = MediaType.APPLICATION_JSON_VALUE)
public class BlockListResource {

    private final BlockListService blockListService;

    public BlockListResource(final BlockListService blockListService) {
        this.blockListService = blockListService;
    }

    @GetMapping
    public ResponseEntity<List<BlockListDTO>> getAllBlockLists() {
        return ResponseEntity.ok(blockListService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockListDTO> getBlockList(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(blockListService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createBlockList(
            @RequestBody @Valid final BlockListDTO blockListDTO) {
        final Integer createdId = blockListService.create(blockListDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateBlockList(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final BlockListDTO blockListDTO) {
        blockListService.update(id, blockListDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlockList(@PathVariable(name = "id") final Integer id) {
        blockListService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
