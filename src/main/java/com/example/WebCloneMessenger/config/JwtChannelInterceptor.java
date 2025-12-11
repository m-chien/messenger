package com.example.WebCloneMessenger.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                authHeaders = accessor.getNativeHeader("authorization");
            }

            if (authHeaders != null && !authHeaders.isEmpty()) {
                try {
                    String token = authHeaders.get(0).replace("Bearer ", "").trim();
                    System.out.println("🔵 Decoding JWT token: " + token.substring(0, Math.min(20, token.length())) + "...");

                    Jwt jwt = jwtDecoder.decode(token);
                    String userId = jwt.getSubject();

                    System.out.println("✅ JWT decoded successfully. UserId: " + userId);

                    // Tạo Authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("USER"))
                            );

                    // SET USER vào accessor
                    accessor.setUser(authentication);

                    System.out.println("✅ Principal set: " + accessor.getUser());

                } catch (Exception ex) {
                    System.err.println("❌ JWT decode failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                System.err.println("❌ No Authorization header found");
            }
        }

        return message;
    }
}