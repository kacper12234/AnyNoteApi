package com.betacom.anynoteapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    @Getter
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long accessExpiration,
                      @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(String login) {
        return generateToken(login, accessExpiration, "access");
    }

    public String generateRefreshToken(String login) {
        return generateToken(login, refreshExpiration, "refresh");
    }

    private String generateToken(String login, long expiration, String type) {
        return Jwts.builder()
                .subject(login)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extractLogin(String token) {
        return extractClaims(token)
                .getSubject();
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractClaims(token).get("type"));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
