package com.hubaccess.domain.financial;

import com.hubaccess.domain.financial.dto.CreatePaRequest;
import com.hubaccess.domain.financial.dto.PriorAuthorizationDto;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases/{caseId}/pa")
@RequiredArgsConstructor
public class PriorAuthorizationController {

    private final PriorAuthorizationService paService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PriorAuthorizationDto>>> getByCaseId(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(paService.getByCaseId(caseId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PriorAuthorizationDto>> create(
            @PathVariable UUID caseId, @Valid @RequestBody CreatePaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(paService.create(caseId, request)));
    }

    @PatchMapping("/{paId}")
    public ResponseEntity<ApiResponse<PriorAuthorizationDto>> update(
            @PathVariable UUID caseId, @PathVariable UUID paId,
            @Valid @RequestBody PriorAuthorizationDto request) {
        return ResponseEntity.ok(ApiResponse.success(paService.update(paId, request)));
    }
}
