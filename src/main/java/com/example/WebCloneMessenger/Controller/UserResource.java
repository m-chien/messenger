package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.AuthResponse;
import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.DTO.UserLoginRequest;
import com.example.WebCloneMessenger.Exception.ApiResponse;
import com.example.WebCloneMessenger.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final Integer createdId = userService.create(userDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateUser(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(id, userDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest user) {
        System.out.println(userService.Login(user));
        return ResponseEntity.ok(userService.Login(user));
    }
    @PostMapping("/upAva")
    public ResponseEntity<ApiResponse<String>> updateAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication authentication) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .code(1001)
                .message("ảnh không hợp lệ")
                .build());
        String email = authentication.getName();
        try {
            String link_ava = userService.updateAvatar(email, file);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .message("Cập nhật thành công avatar")
                    .result(link_ava)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<String>builder()
                            .code(500)
                            .message("Lỗi khi cập nhật avatar: " + e.getMessage())
                            .build());
        }
    }
}
