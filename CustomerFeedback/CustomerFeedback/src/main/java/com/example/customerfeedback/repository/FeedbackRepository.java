package com.example.customerfeedback.repository;

import com.example.customerfeedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);
}