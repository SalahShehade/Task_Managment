package com.example.taskmanagement.controller;

import com.example.taskmanagement.payload.CategoryReport;
import com.example.taskmanagement.payload.SummaryReport;
import com.example.taskmanagement.security.UserDetailsImpl;
import com.example.taskmanagement.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report Controller", description = "Endpoints for generating task reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    /**
     * Generates a summary report of tasks.
     *
     * @param userDetails the authenticated user's details
     * @return SummaryReport DTO
     */
    @Operation(summary = "Generate Summary Report", description = "Provides a summary of total, completed, and pending tasks.")
    @GetMapping("/summary")
    public ResponseEntity<SummaryReport> getSummaryReport(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("Generating summary report for user: {}", userDetails.getUsername());
        SummaryReport summaryReport = reportService.generateSummaryReport(userDetails.getId());
        return ResponseEntity.ok(summaryReport);
    }

    /**
     * Generates a report of tasks grouped by category.
     *
     * @param userDetails the authenticated user's details
     * @return CategoryReport DTO
     */
    @Operation(summary = "Generate Category Report", description = "Provides a count of tasks grouped by category.")
    @GetMapping("/categories")
    public ResponseEntity<CategoryReport> getCategoryReport(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        logger.info("Generating category report for user: {}", userDetails.getUsername());
        CategoryReport categoryReport = reportService.generateCategoryReport(userDetails.getId());
        return ResponseEntity.ok(categoryReport);
    }
}
