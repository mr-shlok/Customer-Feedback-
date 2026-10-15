package com.example.customerfeedback.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 2000)
    public String message;

    public LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    public Feedback() {}

    public Feedback(String message, User user) {
        this.message = message;
        this.user = user;
    }
}