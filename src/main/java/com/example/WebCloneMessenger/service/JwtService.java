package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.Model.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @NonFinal
    @Value("${jwt.secretkey}")
    private String secretKey;
    private final long expirationMs = 50000;
    private final long refreshExpirationDays = 7;
    private final StringRedisTemplate stringRedisTemplate;

    public String createToken(User nguoiDung)
    {
        Date expirateDate = Date.from(Instant.now().plus(expirationMs, ChronoUnit.MILLIS));
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(nguoiDung.getId().toString())
                .issuer("MessengerApp")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(expirateDate)
                .claim("scope", nguoiDung.getRole())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String createRefreshToken(User user) {
        Date expiryDate = Date.from(
                Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS)
        );

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("MessengerApp")
                .issueTime(new Date())
                .expirationTime(expiryDate)
                .jwtID(java.util.UUID.randomUUID().toString()) // 👈 jti
                .build();

        try {
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(new MACSigner(secretKey.getBytes()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveRefreshToken(String refreshToken, String userId) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            stringRedisTemplate.opsForValue().set(
                    "refresh:" + jti,
                    userId,
                    refreshExpirationDays,
                    java.util.concurrent.TimeUnit.DAYS
            );

        } catch (Exception e) {
            throw new RuntimeException("Cannot save refresh token");
        }
    }
    public void logout(String refreshToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            stringRedisTemplate.delete("refresh:" + jti);
        } catch (Exception e) {
            throw new RuntimeException("Logout error");
        }
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            // Parse refresh token
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);

            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid signature");
            }
            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            // Kiểm tra refresh token có tồn tại trong Redis không
            String userId = stringRedisTemplate.opsForValue().get("refresh:" + jti);
            if (userId == null) {
                throw new RuntimeException("Refresh token expired or invalid");
            }

            // Tạo access token mới
            Date expirateDate = Date.from(Instant.now().plus(expirationMs, ChronoUnit.MILLIS));
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issuer("MessengerApp")
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(expirateDate)
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Cannot refresh access token: " + e.getMessage());
        }
    }
}
