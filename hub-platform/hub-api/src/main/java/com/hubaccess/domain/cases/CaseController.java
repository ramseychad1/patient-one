package com.hubaccess.domain.cases;

import com.hubaccess.domain.cases.dto.*;
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
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CaseDto>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(caseService.getCases(pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CaseDto>> create(@Valid @RequestBody CreateCaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(caseService.createCase(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(caseService.getCaseById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseDto>> update(@PathVariable UUID id, @Valid @RequestBody UpdateCaseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(caseService.updateCase(id, request)));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<ApiResponse<List<TimelineEntryDto>>> getTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(caseService.getTimeline(id)));
    }
}
