package com.cpdatabase.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpdatabase.backend.model.SolvedProblem;
import com.cpdatabase.backend.model.User;
import com.cpdatabase.backend.repository.SolvedProblemRespository;
import com.cpdatabase.backend.repository.UserRepository;
import com.cpdatabase.backend.security.JwtUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class ProblemController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SolvedProblemRespository solvedProblemRespository;

    @Autowired
    private JwtUtil jwtUtil;


    private User authenthicationRequest(String authorizationHeader){
        if(authorizationHeader==null||!authorizationHeader.startsWith("Bearer ")){
            throw new RuntimeException("Missing or invalid Authorization header!");
        }
        String token=authorizationHeader.substring(7);
        String username=jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("User profile not found"));
    }


    @PostMapping("/problems/toggle")
    public ResponseEntity<?>toggle(@RequestHeader("Authorization") String authHeader, @RequestParam String problemUrl) {
        try{
            User user=authenthicationRequest(authHeader);
            Optional<SolvedProblem> existingRecord=solvedProblemRespository.findByUserAndProblemUrl(user,problemUrl);
            if(existingRecord.isPresent()){
                solvedProblemRespository.delete(existingRecord.get());
                return ResponseEntity.ok("Removed:"+ problemUrl);
            }
            else{
                SolvedProblem newRecord=new SolvedProblem();
                newRecord.setUser(user);
                newRecord.setProblemUrl(problemUrl);
                solvedProblemRespository.save(newRecord);
                return ResponseEntity.ok("Saved: "+ problemUrl);
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/problems/list")
    public ResponseEntity<?> getSolvedProblem(@RequestHeader("Authorization") String authHeader) {
        try{
            User user=authenthicationRequest(authHeader);
            List<String> urls=solvedProblemRespository.findByUser(user).stream().map(SolvedProblem::getProblemUrl).collect(Collectors.toList());
            return ResponseEntity.ok(urls);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    
    
    
}
