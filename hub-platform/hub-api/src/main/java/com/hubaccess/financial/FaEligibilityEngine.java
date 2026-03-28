package com.hubaccess.financial;

import com.hubaccess.domain.cases.CaseRepository;
import com.hubaccess.domain.cases.HubCase;
import com.hubaccess.domain.financial.FinancialAssistanceCase;
import com.hubaccess.domain.financial.FinancialAssistanceRepository;
import com.hubaccess.domain.financial.dto.FaEligibilityResult;
import com.hubaccess.domain.insurance.InsurancePlan;
import com.hubaccess.domain.insurance.InsurancePlanRepository;
import com.hubaccess.domain.program.ProgramConfig;
import com.hubaccess.domain.program.ProgramConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaEligibilityEngine {

    private static final Set<String> GOVERNMENT_INSURANCE_TYPES = Set.of(
        "MEDICARE_A_B", "MEDICARE_D", "MEDICARE_ADVANTAGE", "MEDICAID", "TRICARE", "VA", "OTHER_GOVERNMENT"
    );

    private final CaseRepository caseRepository;
    private final ProgramConfigRepository configRepository;
    private final InsurancePlanRepository insurancePlanRepository;
    private final FinancialAssistanceRepository faRepository;
    private final FplCalculationService fplService;

    @Transactional
    public List<FaEligibilityResult> evaluate(UUID caseId) {
        HubCase hubCase = caseRepository.findById(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        ProgramConfig config = configRepository.findByProgramId(hubCase.getProgram().getId())
            .orElseThrow(() -> new EntityNotFoundException("Config not found for program"));

        List<InsurancePlan> insurancePlans = insurancePlanRepository.findByHubCaseId(caseId);
        InsurancePlan primaryPlan = insurancePlans.stream()
            .filter(p -> "Primary".equals(p.getPlanSequence()))
            .findFirst().orElse(null);

        String insuranceType = primaryPlan != null ? primaryPlan.getInsuranceTypeCode() : "UNKNOWN";
        boolean isGovernment = GOVERNMENT_INSURANCE_TYPES.contains(insuranceType);

        List<FaEligibilityResult> results = new ArrayList<>();

        // Copay evaluation
        if (Boolean.TRUE.equals(config.getCopayEnabled())) {
            results.add(evaluateCopay(config, insuranceType, isGovernment, caseId));
        }

        // PAP evaluation
        if (Boolean.TRUE.equals(config.getPapEnabled())) {
            results.add(evaluatePap(config, insuranceType, isGovernment, caseId));
        }

        // Bridge evaluation
        if (Boolean.TRUE.equals(config.getBridgeEnabled())) {
            results.add(evaluateBridge(config, hubCase, caseId));
        }

        // Persist results as FinancialAssistanceCase records
        for (FaEligibilityResult result : results) {
            FinancialAssistanceCase fa = FinancialAssistanceCase.builder()
                .hubCase(hubCase)
                .faType(result.getFaType())
                .status(result.getStatus())
                .fplPercentageCalculated(result.getFplPercentage())
                .build();
            faRepository.save(fa);
        }

        return results;
    }

    private FaEligibilityResult evaluateCopay(ProgramConfig config, String insuranceType, boolean isGovernment, UUID caseId) {
        // Federal law: copay assistance NEVER available to government-insured patients
        if (isGovernment) {
            return FaEligibilityResult.builder()
                .faType("Copay").status("Ineligible")
                .reason("Government insurance — copay assistance not available (federal law)")
                .build();
        }

        if (!Set.of("COMMERCIAL", "MARKETPLACE").contains(insuranceType)) {
            return FaEligibilityResult.builder()
                .faType("Copay").status("Ineligible")
                .reason("Insurance type not eligible for copay assistance")
                .build();
        }

        return FaEligibilityResult.builder()
            .faType("Copay").status("Eligible")
            .reason("Commercial/marketplace insurance — eligible for copay assistance")
            .build();
    }

    private FaEligibilityResult evaluatePap(ProgramConfig config, String insuranceType, boolean isGovernment, UUID caseId) {
        boolean eligible = isGovernment || "UNINSURED".equals(insuranceType);
        if (!eligible && Boolean.TRUE.equals(config.getPapAllowCommercialInsured())) {
            eligible = true;
        }

        if (!eligible) {
            return FaEligibilityResult.builder()
                .faType("PAP").status("Ineligible")
                .reason("Insurance type not eligible for PAP")
                .build();
        }

        // FPL check would go here if income data is available
        return FaEligibilityResult.builder()
            .faType("PAP").status("Eligible")
            .reason("Insurance type eligible for PAP — income verification may be required")
            .build();
    }

    private FaEligibilityResult evaluateBridge(ProgramConfig config, HubCase hubCase, UUID caseId) {
        return FaEligibilityResult.builder()
            .faType("Bridge").status("Eligible")
            .reason("Bridge supply available — awaiting trigger confirmation")
            .build();
    }
}
