package com.example.taskmanagement.specification;

import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecifications {

    public static Specification<Task> hasUser(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Task> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Task> hasStatus(Status status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> dueDateBetween(LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("dueDate"), from, to);
    }
}
