package com.example.customerfeedback.controller;

import com.example.customerfeedback.entity.*;
import com.example.customerfeedback.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserRepository users;
    private final FeedbackRepository feedbacks;

    public ApiController(UserRepository users, FeedbackRepository feedbacks) {
        this.users = users;
        this.feedbacks = feedbacks;
    }

    private Map<String, Object> result(String message) {
        return Map.of("message", message);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> d) {
        String name = d.getOrDefault("username", "").trim();
        String email = d.getOrDefault("email", "").trim();
        String pass = d.getOrDefault("password", "");

        if (name.isBlank() || email.isBlank() || pass.length() < 6)
            return ResponseEntity.badRequest().body(result(
                "Enter all fields; password needs 6 characters."));
        if (users.existsByUsername(name) || users.existsByEmail(email))
            return ResponseEntity.badRequest().body(result(
                "Username or email already exists."));

        User u = new User(name, email, pass);
        users.save(u);
        return ResponseEntity.ok(result("Registration successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> d) {
        String name = d.getOrDefault("username", "");
        String pass = d.getOrDefault("password", "");

        if (name.equals("admin") && pass.equals("admin123"))
            return ResponseEntity.ok(Map.of(
                "id", 0, "username", "admin", "role", "ADMIN"));

        return users.findByUsername(name)
            .filter(u -> u.password.equals(pass))
            .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                "id", u.id, "username", u.username, "role", "USER")))
            .orElseGet(() -> ResponseEntity.status(401)
                .body(result("Invalid username or password.")));
    }

    @GetMapping("/feedback/{userId}")
    public List<Feedback> myFeedback(@PathVariable Long userId) {
        return feedbacks.findByUserId(userId);
    }

    @PostMapping("/feedback/{userId}")
    public ResponseEntity<?> add(@PathVariable Long userId,
                                 @RequestBody Map<String, String> d) {
        User u = users.findById(userId).orElse(null);
        String message = d.getOrDefault("message", "").trim();
        if (u == null || message.isBlank())
            return ResponseEntity.badRequest().body(result("Feedback is required."));
        return ResponseEntity.ok(feedbacks.save(new Feedback(message, u)));
    }

    @PutMapping("/feedback/{id}/{userId}")
    public ResponseEntity<?> edit(@PathVariable Long id,
                                  @PathVariable Long userId,
                                  @RequestBody Map<String, String> d) {
        Feedback f = feedbacks.findById(id).orElse(null);
        if (f == null || !f.user.id.equals(userId))
            return ResponseEntity.status(404).body(result("Feedback not found."));
        String message = d.getOrDefault("message", "").trim();
        if (message.isBlank())
            return ResponseEntity.badRequest().body(result("Feedback is required."));
        f.message = message;
        return ResponseEntity.ok(feedbacks.save(f));
    }

    @DeleteMapping("/feedback/{id}/{userId}")
    public ResponseEntity<?> deleteMine(@PathVariable Long id,
                                        @PathVariable Long userId) {
        Feedback f = feedbacks.findById(id).orElse(null);
        if (f == null || !f.user.id.equals(userId))
            return ResponseEntity.status(404).body(result("Feedback not found."));
        feedbacks.delete(f);
        return ResponseEntity.ok(result("Deleted."));
    }

    @GetMapping("/admin/feedback")
    public List<Feedback> allFeedback() {
        return feedbacks.findAll();
    }

    @PutMapping("/admin/feedback/{id}")
    public ResponseEntity<?> adminEdit(@PathVariable Long id,
                                      @RequestBody Map<String, String> d) {
        Feedback f = feedbacks.findById(id).orElse(null);
        if (f == null)
            return ResponseEntity.notFound().build();
        String message = d.getOrDefault("message", "").trim();
        if (message.isBlank())
            return ResponseEntity.badRequest().body(result("Feedback is required."));
        f.message = message;
        return ResponseEntity.ok(feedbacks.save(f));
    }

    @DeleteMapping("/admin/feedback/{id}")
    public ResponseEntity<?> adminDelete(@PathVariable Long id) {
        if (!feedbacks.existsById(id))
            return ResponseEntity.notFound().build();
        feedbacks.deleteById(id);
        return ResponseEntity.ok(result("Deleted."));
    }
}