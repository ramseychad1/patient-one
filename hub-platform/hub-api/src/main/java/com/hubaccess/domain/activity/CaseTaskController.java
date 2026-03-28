package com.hubaccess.domain.activity;

import com.hubaccess.domain.activity.dto.*;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CaseTaskController {

    private final CaseTaskService taskService;

    @GetMapping("/api/v1/cases/{caseId}/tasks")
    public ResponseEntity<ApiResponse<List<CaseTaskDto>>> getTasksByCase(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTasksByCase(caseId)));
    }

    @PostMapping("/api/v1/cases/{caseId}/tasks")
    public ResponseEntity<ApiResponse<CaseTaskDto>> createTask(
            @PathVariable UUID caseId, @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(taskService.createTask(caseId, request)));
    }

    @PatchMapping("/api/v1/cases/{caseId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<CaseTaskDto>> updateTask(
            @PathVariable UUID caseId, @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(taskId, request)));
    }

    @GetMapping("/api/v1/tasks/mine")
    public ResponseEntity<ApiResponse<Page<CaseTaskDto>>> getMyTasks(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getMyTasks(pageable)));
    }
}
