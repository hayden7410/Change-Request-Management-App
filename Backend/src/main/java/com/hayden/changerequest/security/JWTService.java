package com.hayden.changerequest.security;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
private final SecretKey secretKey;
private final long expirationMs;

public JWTService(
    @Value("${security.jwt.secret}") String secret,
    @Value("${security.jwt.expiration-ms}") long expirationMs
){
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMs = expirationMs;
}
public String generateToken(String email){
    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(expirationMs);
    return Jwts.builder()
        .subject(email)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(secretKey)
        .compact();
}
public String extractEmail(String token){
    return extractClaims(token).getSubject();
}
public boolean isTokenValid(String token, String expectedEmail){
    try{
        Claims claims = extractClaims(token);
        return expectedEmail.equals(claims.getSubject());
    }catch(JwtException | IllegalArgumentException exception){
        return false;
    }
}
private Claims extractClaims(String token){
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
}
}
