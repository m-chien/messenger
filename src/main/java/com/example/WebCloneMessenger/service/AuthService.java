package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.AuthResponse;
import com.example.WebCloneMessenger.DTO.UserLoginRequest;
import com.example.WebCloneMessenger.Enum.AuthProvider;
import com.example.WebCloneMessenger.Enum.Role;
import com.example.WebCloneMessenger.Exception.AppException;
import com.example.WebCloneMessenger.Exception.ErrorCode;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.mapper.UserMapper;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final GoogleTokenVerifier verifier;

    public AuthResponse login(UserLoginRequest request) {

        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser == null ||
                !existingUser.getPassword().equals(request.getPass())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 1️⃣ Access token
        String accessToken = jwtService.createToken(existingUser);

        // 2️⃣ Refresh token
        String refreshToken = jwtService.createRefreshToken(existingUser);
        System.out.println("refresh token : "+refreshToken);

        // 3️⃣ Lưu refresh vào Redis
        jwtService.saveRefreshToken(refreshToken, existingUser.getId().toString());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toUserDTO(existingUser))
                .build();
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
            user.setRole(Role.USER.getValue());
            userRepository.save(user);
        }

        String token = jwtService.createToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        jwtService.saveRefreshToken(refreshToken, user.getId().toString());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userMapper.toUserDTO(user))
                .build();
    }

    public void logout(String refreshToken) {
        jwtService.logout(refreshToken);
    }

    public AuthResponse refresh(HttpServletRequest request) {
        System.out.println("chạy vào service refresh token");
        String refreshToken = extractRefreshTokenFromCookie(request);
        System.out.println("chạy xong extract refresh token: " + refreshToken);
        if (refreshToken == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String newAccessToken = jwtService.refreshAccessToken(refreshToken);
        System.out.println("new access token: " + newAccessToken);
        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;


        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}