package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.payload.CategoryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.example.taskmanagement.model.Status;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);

    Page<Task> findByUser(User user, Pageable pageable);

    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, Status status);

    @Query("SELECT t.category AS category, COUNT(t) AS count FROM Task t WHERE t.user.id = :userId GROUP BY t.category")
    List<CategoryCount> countTasksByCategory(@Param("userId") Long userId);
}
