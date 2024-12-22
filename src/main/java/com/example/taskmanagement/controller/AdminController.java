package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.User;
import com.example.taskmanagement.payload.MessageResponse;
import com.example.taskmanagement.payload.UserResponse;
import com.example.taskmanagement.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class AdminController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // Endpoint to get all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        logger.info("Admin is retrieving all users.");
        List<User> users = userRepository.findAll();

        List<UserResponse> userResponses = users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .roles(user.getRoles())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }

    // Endpoint to delete a user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Admin is attempting to delete user with ID {}", id);
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            logger.warn("User not found: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: User not found."));
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully: ID {}", id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully."));
    }



    // Additional admin-specific endpoints can be added here
}
