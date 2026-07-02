package com.cpdatabase.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpdatabase.backend.model.SolvedProblem;
import com.cpdatabase.backend.model.User;
import com.cpdatabase.backend.repository.SolvedProblemRespository;
import com.cpdatabase.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class ProblemController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SolvedProblemRespository solvedProblemRespository;


    @PostMapping("/user/setup")
    public String createTestUser(@RequestParam String username) {
        if(userRepository.findByUsername(username).isPresent()){
            return "User already exist";
        }
        User user=new User();
        user.setUsername(username);
        userRepository.save(user);
        return "created sucessfully";
    }

    @PostMapping("/problems/toggle")
    public String toggleProblem(@RequestParam String username, @RequestParam String problemUrl) {
        User user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User profile not found! Run /users/setup first."));
        
        Optional<SolvedProblem> existingRecord=solvedProblemRespository.findByUserAndProblemUrl(user,problemUrl);
        if(existingRecord.isPresent()){
            solvedProblemRespository.delete(existingRecord.get());
            return "Removed:"+ problemUrl;
        }
        else{
            SolvedProblem newRecord=new SolvedProblem();
            newRecord.setUser(user);
            newRecord.setProblemUrl(problemUrl);
            solvedProblemRespository.save(newRecord);
            return "Saved: "+ problemUrl;
        }
    }

    @GetMapping("/problems/list")
    public List<String> getSolvedProblem(@RequestParam String username) {
        User user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User profile not found! Run /users/setup first."));
        return solvedProblemRespository.findByUser(user).stream().map(SolvedProblem::getProblemUrl).collect(Collectors.toList());
    }
    
    
    
}
