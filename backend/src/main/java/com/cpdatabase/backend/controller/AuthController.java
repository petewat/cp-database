package com.cpdatabase.backend.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpdatabase.backend.repository.UserRepository;
import com.cpdatabase.backend.security.JwtUtil;
import com.cpdatabase.backend.model.User;

import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="*")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

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
        String token=jwtUtil.geenrateToken(username);
        return ResponseEntity.ok(Map.of("token",token,"username",username));
    }
    
}
