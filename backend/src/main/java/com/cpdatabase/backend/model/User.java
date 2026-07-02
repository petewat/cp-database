package com.cpdatabase.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false, length=50)
    private String username;

    public Long getId(){return id;}
    public void setId(Long new_id){id=new_id;}
    public String getUsername(){return username;}
    public void setUsername(String new_username){username=new_username;}
}
