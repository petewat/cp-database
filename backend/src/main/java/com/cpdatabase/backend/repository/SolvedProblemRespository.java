package com.cpdatabase.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cpdatabase.backend.model.SolvedProblem;
import com.cpdatabase.backend.model.User;

public interface SolvedProblemRespository extends JpaRepository<SolvedProblem, Long>{
    List<SolvedProblem> findByUser(User user);
    Optional<SolvedProblem> findByUserAndProblemUrl(User user, String problemUrl);
}
