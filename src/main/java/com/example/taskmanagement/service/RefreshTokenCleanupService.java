package com.example.taskmanagement.service;

import com.example.taskmanagement.repository.RefreshTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class RefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Scheduled task to delete expired refresh tokens.
     * Runs daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredRefreshTokens() {
        int deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        if (deletedCount > 0) {
            System.out.println("Deleted " + deletedCount + " expired refresh tokens.");
        }
    }
}
