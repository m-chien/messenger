package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.UserMapper;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
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

    public void setActiveStatus(int id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        user.setIsOnline(isActive);
        userRepository.save(user);
    }
}
