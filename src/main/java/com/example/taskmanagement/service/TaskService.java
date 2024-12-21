package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.payload.TaskRequest;
import com.example.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Creates a new task for a user based on the TaskRequest.
     *
     * @param taskRequest the task request payload
     * @param user        the user creating the task
     * @return the created Task entity
     */
    public Task createTask(TaskRequest taskRequest, User user) {
        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatus())             // Uses enum
                .priority(taskRequest.getPriority())         // Assuming Priority is an enum
                .category(taskRequest.getCategory())
                .dueDate(taskRequest.getDueDate())
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    /**
     * Retrieves all tasks for a specific user.
     *
     * @param user the user whose tasks are to be retrieved
     * @return list of Task entities
     */
    public List<Task> getAllTasks(User user) {
        return taskRepository.findByUser(user);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the task ID
     * @return Optional containing the Task if found
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Updates an existing task.
     *
     * @param id          the task ID
     * @param taskRequest the task update payload
     * @param userId      the ID of the user attempting the update
     * @return Optional containing the updated Task if successful
     */
    public Optional<Task> updateTask(Long id, TaskRequest taskRequest, Long userId) {
        Optional<Task> existingTaskOpt = taskRepository.findById(id);

        if (existingTaskOpt.isEmpty()) {
            return Optional.empty();
        }

        Task existingTask = existingTaskOpt.get();

        if (!existingTask.getUser().getId().equals(userId)) {
            return Optional.empty();
        }

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setStatus(taskRequest.getStatus());    // Uses enum
        existingTask.setPriority(taskRequest.getPriority()); // Uses enum
        existingTask.setCategory(taskRequest.getCategory());
        existingTask.setDueDate(taskRequest.getDueDate());

        Task updatedTask = taskRepository.save(existingTask);
        return Optional.of(updatedTask);
    }

    /**
     * Deletes a task by its ID if it belongs to the user.
     *
     * @param id     the task ID
     * @param userId the ID of the user attempting the deletion
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteTask(Long id, Long userId) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();

        if (!task.getUser().getId().equals(userId)) {
            return false;
        }

        taskRepository.delete(task);
        return true;
    }
}
