package com.hubaccess.domain.financial;

import com.hubaccess.domain.cases.CaseRepository;
import com.hubaccess.domain.cases.HubCase;
import com.hubaccess.domain.financial.dto.FinancialAssistanceCaseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialAssistanceService {

    private final FinancialAssistanceRepository faRepository;
    private final CaseRepository caseRepository;

    @Transactional(readOnly = true)
    public List<FinancialAssistanceCaseDto> getByCaseId(UUID caseId) {
        return faRepository.findByHubCaseId(caseId).stream().map(this::toDto).toList();
    }

    @Transactional
    public FinancialAssistanceCaseDto update(UUID faId, FinancialAssistanceCaseDto request) {
        FinancialAssistanceCase fa = faRepository.findById(faId)
            .orElseThrow(() -> new EntityNotFoundException("FA case not found: " + faId));
        if (request.getStatus() != null) fa.setStatus(request.getStatus());
        if (request.getAnnualHouseholdIncome() != null) fa.setAnnualHouseholdIncome(request.getAnnualHouseholdIncome());
        if (request.getHouseholdSize() != null) fa.setHouseholdSize(request.getHouseholdSize());
        if (request.getIncomeVerified() != null) fa.setIncomeVerified(request.getIncomeVerified());
        if (request.getIncomeVerificationMethod() != null) fa.setIncomeVerificationMethod(request.getIncomeVerificationMethod());
        if (request.getNotes() != null) fa.setNotes(request.getNotes());
        return toDto(faRepository.save(fa));
    }

    private FinancialAssistanceCaseDto toDto(FinancialAssistanceCase fa) {
        return FinancialAssistanceCaseDto.builder()
            .id(fa.getId()).caseId(fa.getHubCase().getId())
            .faType(fa.getFaType()).status(fa.getStatus())
            .annualHouseholdIncome(fa.getAnnualHouseholdIncome())
            .householdSize(fa.getHouseholdSize())
            .fplPercentageCalculated(fa.getFplPercentageCalculated())
            .incomeVerified(fa.getIncomeVerified())
            .incomeVerificationMethod(fa.getIncomeVerificationMethod())
            .eligibilityDeterminedAt(fa.getEligibilityDeterminedAt())
            .eligibilityDeterminedBy(fa.getEligibilityDeterminedBy() != null ? fa.getEligibilityDeterminedBy().getId() : null)
            .approvalDate(fa.getApprovalDate())
            .expiryDate(fa.getExpiryDate())
            .benefitAmountMonthly(fa.getBenefitAmountMonthly())
            .benefitYtdUsed(fa.getBenefitYtdUsed())
            .bridgeSupplyDaysAuthorized(fa.getBridgeSupplyDaysAuthorized())
            .bridgeShipDate(fa.getBridgeShipDate())
            .denialReason(fa.getDenialReason())
            .notes(fa.getNotes())
            .build();
    }
}
