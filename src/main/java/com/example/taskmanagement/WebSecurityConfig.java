package com.example.taskmanagement;

import com.example.taskmanagement.security.AuthEntryPointJwt;
import com.example.taskmanagement.security.AuthTokenFilter;
import com.example.taskmanagement.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * Bean for AuthenticationManager to handle authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean for PasswordEncoder using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure CORS to allow requests from specified origins.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from this origin
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Adjust as needed

        // Allow these HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow these headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Allow credentials (e.g., cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Expose headers (optional)
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS configuration to all endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configure the security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with the configuration defined above
                .cors(Customizer.withDefaults())

                // Disable CSRF since we're using JWT
                .csrf(csrf -> csrf.disable())

                // Set session management to stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Handle unauthorized access attempts
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))

                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Permit all requests to /api/auth/** and /h2-console/**
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()

                        // Admin-specific endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Any other request must be authenticated
                        .anyRequest().authenticated())

                // Allow frames from the same origin to support H2 Console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))

                // Add JWT authentication filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
