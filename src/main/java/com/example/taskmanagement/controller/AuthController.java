package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.ERole;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.payload.JwtResponse;
import com.example.taskmanagement.payload.LoginRequest;
import com.example.taskmanagement.payload.MessageResponse;
import com.example.taskmanagement.payload.SignupRequest;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.JwtUtils;
import com.example.taskmanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Existing Registration Endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("Registering user: {}", signUpRequest.getUsername());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.warn("Username {} is already taken.", signUpRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Email {} is already in use.", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account with ROLE_USER
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(Set.of(ERole.ROLE_USER))
                .build();

        userRepository.save(user);

        logger.info("User {} registered successfully.", signUpRequest.getUsername());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }



    // Corrected Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            logger.info("User {} authenticated successfully.", loginRequest.getUsername());

            return ResponseEntity.ok(JwtResponse.builder()
                    .token(jwt)
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .build());
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {}", loginRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Invalid username or password!"));
        }
    }
}
