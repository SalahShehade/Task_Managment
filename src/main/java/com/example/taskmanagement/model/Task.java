package com.example.taskmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // Enum: PENDING, IN_PROGRESS, COMPLETED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority; // Enum: HIGH, MEDIUM, LOW

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // Enum: WORK, PERSONAL, LEARNING, OTHERS

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
