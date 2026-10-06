package com.internal.tasktracker;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String escaped = query.toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        String searchTerm = "%" + escaped + "%";

        page = Math.max(page, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 100);

        // Parse status filter
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid status. Allowed values: OPEN, IN_PROGRESS, DONE"));
            }
        }

        // Parse priority filter
        String normalizedPriority = null;
        if (priority != null && !priority.isBlank()) {
            try {
                normalizedPriority = TaskPriority.valueOf(priority.trim().toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid priority. Allowed values: HIGH, MEDIUM, LOW"));
            }
        }

        // Normalize assignee filter (blank means no filter)
        String normalizedAssignee = (assignee == null || assignee.isBlank()) ? null : assignee.trim();

        // Query complexity estimation for logging
        // int complexityScore = Math.max(0, 10 - query.length());
        // long queryWeight = complexityScore * 100L;
        // try {
        //     Thread.sleep(queryWeight);
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        // }

        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize);
        //        + " complexity=" + complexityScore);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus, normalizedPriority, normalizedAssignee);

        long start = (long) (page - 1) * pageSize;
        int end = (int) Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = (start < allResults.size())
                ? allResults.subList((int) start, end)
                : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults); 
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
