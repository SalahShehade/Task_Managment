package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.RefreshToken;
import com.example.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByToken(String token);
    int deleteByUser(User user);
    int deleteByExpiryDateBefore(Instant expiryDate);
}
