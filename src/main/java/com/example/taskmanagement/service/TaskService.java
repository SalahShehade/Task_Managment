package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.payload.TaskRequest;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.specification.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .status(taskRequest.getStatus())
                .priority(taskRequest.getPriority())
                .category(taskRequest.getCategory())
                .dueDate(taskRequest.getDueDate())
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    /**
     * Retrieves all tasks for a specific user with optional filters.
     *
     * @param user        the user whose tasks are to be retrieved
     * @param title       optional title filter
     * @param status      optional status filter
     * @param category    optional category filter
     * @param priority    optional priority filter
     * @param dueDateFrom optional due date from filter
     * @param dueDateTo   optional due date to filter
     * @param sortBy      field to sort by
     * @param sortDir     sort direction (asc/desc)
     * @param page        page number for pagination
     * @param size        page size for pagination
     * @return list of Task entities
     */
    public Page<Task> getAllTasks(User user, String title, Status status, String category, Priority priority,
                                  LocalDate dueDateFrom, LocalDate dueDateTo, String sortBy, String sortDir,
                                  int page, int size) {

        Specification<Task> spec = Specification.where(TaskSpecifications.hasUser(user));

        if (title != null && !title.isEmpty()) {
            spec = spec.and(TaskSpecifications.hasTitle(title));
        }
        if (status != null) {
            spec = spec.and(TaskSpecifications.hasStatus(status));
        }
        if (category != null && !category.isEmpty()) {
            spec = spec.and(TaskSpecifications.hasCategory(category));
        }
        if (priority != null) {
            spec = spec.and(TaskSpecifications.hasPriority(priority));
        }
        if (dueDateFrom != null && dueDateTo != null) {
            spec = spec.and(TaskSpecifications.dueDateBetween(dueDateFrom, dueDateTo));
        }

        // Handle Sorting
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return taskRepository.findAll(spec, pageable);
    }

    /**
     * Retrieves a task by its ID if it belongs to the user.
     *
     * @param id   the task ID
     * @param user the user requesting the task
     * @return Optional containing the Task if found and owned by user
     */
    public Optional<Task> getTaskById(Long id, User user) {
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(user.getId()));
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
        existingTask.setStatus(taskRequest.getStatus());
        existingTask.setPriority(taskRequest.getPriority());
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
