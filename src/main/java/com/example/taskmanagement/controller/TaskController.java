package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.payload.MessageResponse;
import com.example.taskmanagement.payload.TaskRequest;
import com.example.taskmanagement.payload.TaskResponse;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "APIs for managing tasks")
@SecurityRequirement(name = "bearerAuth") // Applies security scheme globally to this controller
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    // Create a new task
    @Operation(summary = "Create a new task", description = "Creates a new task for the authenticated user.")
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is creating a new task: {}", userDetails.getUsername(), taskRequest.getTitle());

        Optional<User> userOptional = userRepository.findById(userDetails.getId());

        if (userOptional.isEmpty()) {
            logger.error("User not found: ID {}", userDetails.getId());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        User user = userOptional.get();

        Task createdTask = taskService.createTask(taskRequest, user);

        logger.info("Task created successfully: ID {}", createdTask.getId());

        return ResponseEntity.ok(TaskResponse.fromEntity(createdTask));
    }

    // Get all tasks for the authenticated user
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks for the authenticated user.")
    @GetMapping
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is retrieving all tasks.", userDetails.getUsername());

        Optional<User> userOptional = userRepository.findById(userDetails.getId());

        if (userOptional.isEmpty()) {
            logger.error("User not found: ID {}", userDetails.getId());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        User user = userOptional.get();
        List<TaskResponse> taskResponses = taskService.getAllTasks(user)
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());

        logger.info("User {} retrieved {} tasks.", userDetails.getUsername(), taskResponses.size());

        return ResponseEntity.ok(taskResponses);
    }

    // Get a specific task by ID
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is retrieving task with ID {}", userDetails.getUsername(), id);

        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (taskOptional.isEmpty()) {
            logger.warn("Task not found: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: Task not found."));
        }

        Task task = taskOptional.get();

        if (!task.getUser().getId().equals(userDetails.getId())) {
            logger.warn("User {} attempted to access task {} belonging to user ID {}",
                    userDetails.getUsername(), id, task.getUser().getId());
            return ResponseEntity.status(403).body(new MessageResponse("Error: Access denied."));
        }

        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    // Update an existing task
    @Operation(summary = "Update task", description = "Updates an existing task by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is updating task with ID {}", userDetails.getUsername(), id);

        Optional<Task> updatedTaskOptional = taskService.updateTask(id, taskRequest, userDetails.getId());

        if (updatedTaskOptional.isEmpty()) {
            logger.warn("Task not found or access denied: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: Task not found or access denied."));
        }

        Task updatedTask = updatedTaskOptional.get();

        // Convert to DTO
        TaskResponse taskResponse = TaskResponse.fromEntity(updatedTask);

        logger.info("Task updated successfully: ID {}", updatedTask.getId());

        return ResponseEntity.ok(taskResponse);
    }

    // Delete a task by ID
    @Operation(summary = "Delete task", description = "Deletes a task by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is deleting task with ID {}", userDetails.getUsername(), id);

        boolean isDeleted = taskService.deleteTask(id, userDetails.getId());

        if (!isDeleted) {
            logger.warn("Task not found or access denied: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: Task not found or access denied."));
        }

        logger.info("Task deleted successfully: ID {}", id);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully."));
    }
}
