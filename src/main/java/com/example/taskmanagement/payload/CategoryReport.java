package com.example.taskmanagement.payload;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CategoryReport {
    private Map<String, Long> tasksByCategory;
}
