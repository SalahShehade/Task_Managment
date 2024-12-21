package com.example.taskmanagement.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
    @NotBlank(message = "Refresh token is mandatory")
    private String refreshToken;
}
