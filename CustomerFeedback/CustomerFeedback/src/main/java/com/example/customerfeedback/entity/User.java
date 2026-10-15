package com.example.customerfeedback.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String username;
    public String email;
    public String password;
    public String role = "USER";

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}