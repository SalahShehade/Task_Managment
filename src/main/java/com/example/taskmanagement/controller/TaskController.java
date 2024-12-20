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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    // Create a new task
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is creating a new task: {}", userDetails.getUsername(), taskRequest.getTitle());

        Optional<User> userOptional = userRepository.findById(userDetails.getId());

        if (!userOptional.isPresent()) {
            logger.error("User not found: ID {}", userDetails.getId());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        User user = userOptional.get();

        // Map TaskRequest to Task entity
        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatus())
                .user(user)
                .build();

        Task createdTask = taskService.createTask(task);

        // Convert to DTO
        TaskResponse taskResponse = TaskResponse.builder()
                .id(createdTask.getId())
                .title(createdTask.getTitle())
                .description(createdTask.getDescription())
                .status(createdTask.getStatus())
                .build();

        logger.info("Task created successfully: ID {}", createdTask.getId());

        return ResponseEntity.ok(taskResponse);
    }

    // Get all tasks for the authenticated user
    @GetMapping
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is retrieving all tasks.", userDetails.getUsername());

        Optional<User> userOptional = userRepository.findById(userDetails.getId());

        if (!userOptional.isPresent()) {
            logger.error("User not found: ID {}", userDetails.getId());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        User user = userOptional.get();
        List<Task> tasks = taskService.getAllTasks(user);

        // Convert to DTOs
        List<TaskResponse> taskResponses = tasks.stream()
                .map(task -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .build())
                .collect(Collectors.toList());

        logger.info("User {} retrieved {} tasks.", userDetails.getUsername(), taskResponses.size());

        return ResponseEntity.ok(taskResponses);
    }

    // Get a specific task by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is retrieving task with ID {}", userDetails.getUsername(), id);

        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (!taskOptional.isPresent()) {
            logger.warn("Task not found: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: Task not found."));
        }

        Task task = taskOptional.get();

        if (!task.getUser().getId().equals(userDetails.getId())) {
            logger.warn("User {} attempted to access task {} belonging to user ID {}",
                    userDetails.getUsername(), id, task.getUser().getId());
            return ResponseEntity.status(403).body(new MessageResponse("Error: Access denied."));
        }

        // Convert to DTO
        TaskResponse taskResponse = TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();

        logger.info("Task retrieved successfully: ID {}", id);

        return ResponseEntity.ok(taskResponse);
    }

    // Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is updating task with ID {}", userDetails.getUsername(), id);

        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (!taskOptional.isPresent()) {
            logger.warn("Task not found: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: Task not found."));
        }

        Task existingTask = taskOptional.get();

        if (!existingTask.getUser().getId().equals(userDetails.getId())) {
            logger.warn("User {} attempted to update task {} belonging to user ID {}",
                    userDetails.getUsername(), id, existingTask.getUser().getId());
            return ResponseEntity.status(403).body(new MessageResponse("Error: Access denied."));
        }

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setStatus(taskRequest.getStatus());

        Task updatedTask = taskService.updateTask(existingTask);

        // Convert to DTO
        TaskResponse taskResponse = TaskResponse.builder()
                .id(updatedTask.getId())
                .title(updatedTask.getTitle())
                .description(updatedTask.getDescription())
                .status(updatedTask.getStatus())
                .build();

        logger.info("Task updated successfully: ID {}", updatedTask.getId());

        return ResponseEntity.ok(taskResponse);
    }

    // Delete a task by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("User {} is deleting task with ID {}", userDetails.getUsername(), id);

        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (!taskOptional.isPresent()) {
            logger.warn("Task not found: ID {}", id);
            return ResponseEntity.status(404).body(new MessageResponse("Error: Task not found."));
        }

        Task task = taskOptional.get();

        if (!task.getUser().getId().equals(userDetails.getId())) {
            logger.warn("User {} attempted to delete task {} belonging to user ID {}",
                    userDetails.getUsername(), id, task.getUser().getId());
            return ResponseEntity.status(403).body(new MessageResponse("Error: Access denied."));
        }

        taskService.deleteTask(id);
        logger.info("Task deleted successfully: ID {}", id);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully."));
    }
}
