package com.example.taskmanagement.payload;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.Category;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Category category;
    private LocalDate dueDate;

    /**
     * Converts a Task entity to a TaskResponse DTO.
     *
     * @param task the Task entity
     * @return TaskResponse DTO
     */
    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .category(task.getCategory())
                .dueDate(task.getDueDate())
                .build();
    }
}
