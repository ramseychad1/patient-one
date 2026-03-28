package com.hubaccess.domain.insurance;

import com.hubaccess.domain.insurance.dto.*;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases/{caseId}")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    @GetMapping("/insurance")
    public ResponseEntity<ApiResponse<List<InsurancePlanDto>>> getInsurancePlans(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(insuranceService.getInsurancePlans(caseId)));
    }

    @PostMapping("/insurance")
    public ResponseEntity<ApiResponse<InsurancePlanDto>> createInsurancePlan(
            @PathVariable UUID caseId, @Valid @RequestBody CreateInsurancePlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(insuranceService.createInsurancePlan(caseId, request)));
    }

    @GetMapping("/bv")
    public ResponseEntity<ApiResponse<List<BenefitsVerificationDto>>> getBenefitsVerifications(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(insuranceService.getBenefitsVerifications(caseId)));
    }

    @PostMapping("/bv")
    public ResponseEntity<ApiResponse<BenefitsVerificationDto>> createBenefitsVerification(@PathVariable UUID caseId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(insuranceService.createBenefitsVerification(caseId)));
    }
}
