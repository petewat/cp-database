package com.cpdatabase.backend.model;

import jakarta.persistence.*;


@Entity
@Table(name = "solved_problems")
public class SolvedProblem{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable=false)
    private User user;

    @Column(name = "problem_url",nullable=false,length=512)
    private String problemUrl;


    public SolvedProblem(){}
    public SolvedProblem(User new_user, String new_problemUrl){
        user=new_user;
        problemUrl=new_problemUrl;
    }
    public Long getId(){return id;}
    public User getUser(){return user;}
    public String getProblemUrl(){return problemUrl;}
    public void setUser(User user){this.user=user;}
    public void setProblemUrl(String problemUrl){this.problemUrl=problemUrl;}
}