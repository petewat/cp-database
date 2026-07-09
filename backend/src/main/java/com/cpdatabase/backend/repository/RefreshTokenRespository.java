package com.cpdatabase.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cpdatabase.backend.model.RefreshToken;
import com.cpdatabase.backend.model.User;

public interface RefreshTokenRespository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken>findByToken(String token);   
    void deleteByUser(User user);
}