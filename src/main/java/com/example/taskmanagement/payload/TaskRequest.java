package com.example.taskmanagement.payload;

import com.example.taskmanagement.model.Category;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotNull(message = "Status is mandatory")
    private Status status;

    @NotNull(message = "Priority is mandatory")
    private Priority priority;

    @NotNull(message = "Category is mandatory")
    private Category category;

    @NotNull(message = "Due date is mandatory")
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;
}
