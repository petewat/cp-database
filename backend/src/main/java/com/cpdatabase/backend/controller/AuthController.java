package com.cpdatabase.backend.controller;


import java.beans.Transient;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpdatabase.backend.repository.RefreshTokenRespository;
import com.cpdatabase.backend.repository.UserRepository;
import com.cpdatabase.backend.security.JwtUtil;

import jakarta.transaction.Transactional;

import com.cpdatabase.backend.model.RefreshToken;
import com.cpdatabase.backend.model.User;

import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final long EXPIRATION_TIME=900000;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRespository refreshTokenRespository;


    @Transactional
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshTokenStr, @RequestParam String accessTokenStr){
        try{
            if(!jwtUtil.isTokenExpired(accessTokenStr))return ResponseEntity.ok("ok");
        }catch(Exception e){
            
        }
        Optional<RefreshToken> tokenOpt=refreshTokenRespository.findByToken(refreshTokenStr);
        if(tokenOpt.isEmpty()){
            return ResponseEntity.status(401).body("Invalid token");
        }
        RefreshToken storedToken=tokenOpt.get();
        User user=storedToken.getUser();
        if(storedToken.isRevoked()){
            refreshTokenRespository.deleteByUser(user);
            return ResponseEntity.status(403).body("Security Alert: Token reuse detected! Logged out everywhere.");
        }
        if(jwtUtil.isTokenExpired(refreshTokenStr)){
            return ResponseEntity.status(401).body("Refresh token expired. Log in again.");
        }
        storedToken.setRevoked(true);
        refreshTokenRespository.save(storedToken);

        String newAccessToken=jwtUtil.generateToken(user.getUsername(),EXPIRATION_TIME);
        String newRefreshTokenStr=jwtUtil.generateToken(user.getUsername(),EXPIRATION_TIME*2);

        RefreshToken newRefreshToken=new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenStr);
        newRefreshToken.setUser(user);
        newRefreshToken.setRevoked(false);
        refreshTokenRespository.save(newRefreshToken);
        return ResponseEntity.ok(Map.of("accessToken",newAccessToken,"refreshToken",newRefreshTokenStr));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        if(userRepository.findByUsername(username).isPresent()){
            return ResponseEntity.badRequest().body("Username already taken");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,@RequestParam String password){
        var userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(404).body("User not found");
        }
        User user = userOptional.get();

        if(!user.getPassword().equals(password)){
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token=jwtUtil.generateToken(username,EXPIRATION_TIME);
        String newRefreshTokenStr=jwtUtil.generateToken(user.getUsername(),EXPIRATION_TIME*2);

        RefreshToken newRefreshToken=new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenStr);
        newRefreshToken.setUser(user);
        newRefreshToken.setRevoked(false);
        refreshTokenRespository.save(newRefreshToken);

        return ResponseEntity.ok(Map.of("accessToken",token,"username",username,"refreshToken",newRefreshTokenStr));
    }

}
