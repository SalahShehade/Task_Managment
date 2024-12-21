package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.payload.CategoryReport;
import com.example.taskmanagement.payload.CategoryCount;
import com.example.taskmanagement.payload.SummaryReport;
import com.example.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TaskRepository taskRepository;

    /**
     * Generates a summary report of tasks.
     *
     * @param userId the ID of the user
     * @return SummaryReport DTO
     */
    public SummaryReport generateSummaryReport(Long userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, Status.COMPLETED);
        long pendingTasks = taskRepository.countByUserIdAndStatus(userId, Status.PENDING);

        return SummaryReport.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .build();
    }

    /**
     * Generates a report of tasks grouped by category.
     *
     * @param userId the ID of the user
     * @return CategoryReport DTO
     */
    public CategoryReport generateCategoryReport(Long userId) {
        List<CategoryCount> categoryCounts = taskRepository.countTasksByCategory(userId);
        Map<String, Long> tasksByCategory = new HashMap<>();

        for (CategoryCount cc : categoryCounts) {
            tasksByCategory.put(cc.getCategory(), cc.getCount());
        }

        return CategoryReport.builder()
                .tasksByCategory(tasksByCategory)
                .build();
    }
}
