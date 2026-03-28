package com.hubaccess.domain.financial;

import com.hubaccess.domain.financial.dto.FinancialAssistanceCaseDto;
import com.hubaccess.domain.financial.dto.FaEligibilityResult;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases/{caseId}/financial-assistance")
@RequiredArgsConstructor
public class FinancialAssistanceController {

    private final FinancialAssistanceService faService;
    private final com.hubaccess.financial.FaEligibilityEngine eligibilityEngine;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialAssistanceCaseDto>>> getByCaseId(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(faService.getByCaseId(caseId)));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<List<FaEligibilityResult>>> evaluate(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(eligibilityEngine.evaluate(caseId)));
    }

    @PatchMapping("/{faId}")
    public ResponseEntity<ApiResponse<FinancialAssistanceCaseDto>> update(
            @PathVariable UUID caseId, @PathVariable UUID faId,
            @Valid @RequestBody FinancialAssistanceCaseDto request) {
        return ResponseEntity.ok(ApiResponse.success(faService.update(faId, request)));
    }
}
