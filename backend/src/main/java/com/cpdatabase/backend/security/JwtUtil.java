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
    private final long EXPIRATION_TIME=86400000;

    public String geenrateToken(String username){
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
    private boolean isTokenExpired(String token){
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