package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.AuthResponse;
import com.example.WebCloneMessenger.DTO.GoogleLoginRequest;
import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.DTO.UserLoginRequest;
import com.example.WebCloneMessenger.Exception.ApiResponse;
import com.example.WebCloneMessenger.service.AuthService;
import com.example.WebCloneMessenger.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;
    private final AuthService authService;

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
    public ResponseEntity<AuthResponse> login(
            @RequestBody UserLoginRequest user,
            HttpServletResponse response) {

        AuthResponse auth = authService.login(user);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", auth.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        System.out.println("Cookie gửi về client: " + cookie.toString());
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        System.out.println("Set-Cookie từ response header: " + response.getHeader(HttpHeaders.SET_COOKIE));

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(auth.getToken())
                        .refreshToken(auth.getRefreshToken())
                        .user(auth.getUser())
                        .build()
        );
    }
    @PostMapping("/auth/google")
    public ResponseEntity<AuthResponse> loginGoogle(
            @RequestBody GoogleLoginRequest request,
            HttpServletResponse response) throws Exception {

        AuthResponse auth = authService.loginWithGoogle(request.getIdToken());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", auth.getRefreshToken())
                .httpOnly(true)
                .secure(false) // production nên true
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(auth.getToken())
                        .refreshToken(auth.getRefreshToken())
                        .user(auth.getUser())
                        .build()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
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
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        System.out.println("gọi refresh token");
        return ResponseEntity.ok(authService.refresh(request));
    }
}
