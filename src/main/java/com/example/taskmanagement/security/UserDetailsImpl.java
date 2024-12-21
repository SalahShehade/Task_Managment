package com.example.taskmanagement.security;

import com.example.taskmanagement.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of UserDetails to provide user information to Spring Security.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Builds a UserDetailsImpl object from a User entity.
     *
     * @param user the User entity
     * @return UserDetailsImpl object
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // The following can be customized as per requirements
    @Override
    public boolean isAccountNonExpired() {
        return true; // Modify based on your requirements
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Modify based on your requirements
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Modify based on your requirements
    }

    @Override
    public boolean isEnabled() {
        return true; // Modify based on your requirements
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
