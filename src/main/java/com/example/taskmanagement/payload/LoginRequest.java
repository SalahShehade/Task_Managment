package com.example.taskmanagement.payload;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
