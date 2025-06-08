package com.project.user_feed_personalization.controller;

import com.project.user_feed_personalization.dto.LoginRequest;
import com.project.user_feed_personalization.dto.LoginResponse;
import com.project.user_feed_personalization.dto.RegisterRequest;
import com.project.user_feed_personalization.entity.User;
import com.project.user_feed_personalization.service.UserService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .tagAffinityScores(new HashMap<>())
                .tagInteractionCounts(new HashMap<>())
                .interestedTags(new ArrayList<>())
                .build();

        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (user != null && user.getPassword().equals(request.getPassword())) {
            LoginResponse response = LoginResponse.builder()
                    .user(user)
                    .message("Login Successful")
                    .build();
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse.builder().message("Invalid Credentials").build());
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/preferences/tags")
    public ResponseEntity<Void> updateTagPreferences(
            @RequestParam String email,
            @RequestBody List<String> tagIds) {
        userService.updateUserTagsPreferences(email, tagIds);
        return ResponseEntity.ok().build();
    }
}
