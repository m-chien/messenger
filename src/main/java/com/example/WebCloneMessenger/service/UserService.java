package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.AuthResponse;
import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.DTO.UserLoginRequest;
import com.example.WebCloneMessenger.Exception.AppException;
import com.example.WebCloneMessenger.Exception.ErrorCode;
import com.example.WebCloneMessenger.Model.AuthProvider;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.UserMapper;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final GoogleTokenVerifier verifier;


    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream().map(userMapper::toUserDTO)
                .toList();
    }

    public UserDTO get(final Integer id) {
        return userRepository.findById(id).map(userMapper::toUserDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UserDTO userDTO) {
        final User user = userMapper.toEntity(userDTO);
        return userRepository.save(user).getId();
    }

    public void update(final Integer id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        userMapper.toEntity(userDTO);
        userRepository.save(user);
    }

    public void delete(final Integer id) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteUser(id));
        userRepository.delete(user);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setIsOnline(user.getIsOnline());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setIsOnline(userDTO.getIsOnline());
        user.setAvatarUrl(userDTO.getAvatarUrl());
        return user;
    }

    public AuthResponse Login(UserLoginRequest user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPass())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtService.createToken(existingUser);
        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toUserDTO(existingUser))
                .build();
    }

    public String updateAvatar(String email, MultipartFile file) throws IOException {
        // tìm người dùng từ email đã giải mã
        User tokenUser = userRepository.findByEmail(email);
        if (tokenUser.getEmail() == null) {
            throw new RuntimeException("Bạn không có quyền cập nhật thông tin của người dùng khác");
        }
        // 5. Lấy extension gốc
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        List<String> allowed = Arrays.asList(".png", ".jpg", ".jpeg", ".webp");
        if (!allowed.contains(extension)) throw new RuntimeException("File type not allowed");

        String fileName = tokenUser.getId() + "_ava" + extension;
        // 6. Tạo folder nếu chưa có
        Path path = Paths.get("img_user/user_avatar", fileName);
        Files.createDirectories(path.getParent());
        // 7. Lưu file
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Avatar saved to: " + path.toAbsolutePath());
        // Cập nhật avatar URL cho user
        String avatarUrl = "/img_user/user_avatar/" + fileName;
        tokenUser.setAvatarUrl(avatarUrl);
        userRepository.save(tokenUser);
        return avatarUrl;
    }

    public AuthResponse loginWithGoogle(String idToken) throws Exception {
        GoogleIdToken.Payload payload = verifier.verify(idToken);

        if (!payload.getEmailVerified()) {
            throw new RuntimeException("Google email not verified");
        }

        String googleId = payload.getSubject(); // sub
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String avatar = (String) payload.get("picture");

        User user = userRepository
                .findByProviderIdAndProvider(googleId, AuthProvider.GOOGLE.name());

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAvatarUrl(avatar);
            user.setProvider(AuthProvider.GOOGLE.name());
            user.setProviderId(googleId);
            userRepository.save(user);
        }

        String token = jwtService.createToken(user);

        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toUserDTO(user))
                .build();
    }


    public void setActiveStatus(int id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        user.setIsOnline(isActive);
        userRepository.save(user);
    }
}
