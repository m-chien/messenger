package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.Model.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
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
    private final long expirationMs = 3600000;

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
}
