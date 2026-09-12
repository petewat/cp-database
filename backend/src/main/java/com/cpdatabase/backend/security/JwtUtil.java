package com.cpdatabase.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil{   
    private final SecretKey SECRET_KEY=Jwts.SIG.HS256.key().build();

    public String generateToken(String username, Long EXPIRATION_TIME){
        return Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SECRET_KEY)
        .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser()
        .verifyWith(SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
    }
    public boolean isTokenExpired(String token){
        Date expiration=Jwts.parser()
        .verifyWith(SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();
        return expiration.before(new Date());
    }
    public boolean validateToken(String token, String username){
        return (extractUsername(token).equals(username)&&!isTokenExpired(token));
    }
}