package com.hubaccess.domain.program;

import com.hubaccess.domain.program.dto.*;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProgramDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(programService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<ProgramDto>> create(@Valid @RequestBody CreateProgramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(programService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgramDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(programService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<ProgramDto>> update(@PathVariable UUID id, @Valid @RequestBody CreateProgramRequest request) {
        return ResponseEntity.ok(ApiResponse.success(programService.update(id, request)));
    }

    @GetMapping("/{id}/config")
    public ResponseEntity<ApiResponse<ProgramConfigDto>> getConfig(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(programService.getConfig(id)));
    }

    @PutMapping("/{id}/config")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<ProgramConfigDto>> updateConfig(@PathVariable UUID id, @Valid @RequestBody UpdateProgramConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.success(programService.updateConfig(id, request)));
    }
}
