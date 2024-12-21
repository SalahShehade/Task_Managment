package com.example.taskmanagement.payload;

import com.example.taskmanagement.model.ERole;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<ERole> roles;
}
