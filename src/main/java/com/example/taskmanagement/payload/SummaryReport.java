package com.example.taskmanagement.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryReport {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
}
