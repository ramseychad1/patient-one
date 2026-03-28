package com.hubaccess.domain.insurance;

import com.hubaccess.domain.cases.CaseRepository;
import com.hubaccess.domain.cases.HubCase;
import com.hubaccess.domain.insurance.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsurancePlanRepository insurancePlanRepository;
    private final BenefitsVerificationRepository bvRepository;
    private final CaseRepository caseRepository;

    @Transactional(readOnly = true)
    public List<InsurancePlanDto> getInsurancePlans(UUID caseId) {
        return insurancePlanRepository.findByHubCaseId(caseId).stream()
            .map(this::toInsuranceDto).toList();
    }

    @Transactional
    public InsurancePlanDto createInsurancePlan(UUID caseId, CreateInsurancePlanRequest request) {
        HubCase hubCase = caseRepository.findById(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        InsurancePlan plan = InsurancePlan.builder()
            .hubCase(hubCase)
            .planSequence(request.getPlanSequence())
            .insuranceTypeCode(request.getInsuranceTypeCode())
            .payerName(request.getPayerName())
            .planName(request.getPlanName())
            .memberId(request.getMemberId())
            .groupId(request.getGroupId())
            .rxBin(request.getRxBin())
            .rxPcn(request.getRxPcn())
            .effectiveDate(request.getEffectiveDate())
            .termDate(request.getTermDate())
            .build();

        return toInsuranceDto(insurancePlanRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public List<BenefitsVerificationDto> getBenefitsVerifications(UUID caseId) {
        return bvRepository.findByHubCaseId(caseId).stream()
            .map(this::toBvDto).toList();
    }

    @Transactional
    public BenefitsVerificationDto createBenefitsVerification(UUID caseId) {
        HubCase hubCase = caseRepository.findById(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        BenefitsVerification bv = BenefitsVerification.builder()
            .hubCase(hubCase)
            .build();

        return toBvDto(bvRepository.save(bv));
    }

    private InsurancePlanDto toInsuranceDto(InsurancePlan p) {
        return InsurancePlanDto.builder()
            .id(p.getId()).caseId(p.getHubCase().getId())
            .planSequence(p.getPlanSequence()).insuranceTypeCode(p.getInsuranceTypeCode())
            .payerName(p.getPayerName()).planName(p.getPlanName())
            .memberId(p.getMemberId()).groupId(p.getGroupId())
            .rxBin(p.getRxBin()).rxPcn(p.getRxPcn())
            .effectiveDate(p.getEffectiveDate()).termDate(p.getTermDate())
            .build();
    }

    private BenefitsVerificationDto toBvDto(BenefitsVerification bv) {
        return BenefitsVerificationDto.builder()
            .id(bv.getId()).caseId(bv.getHubCase().getId())
            .status(bv.getStatus()).benefitType(bv.getBenefitType())
            .coverageConfirmed(bv.getCoverageConfirmed())
            .paRequiredPerBv(bv.getPaRequiredPerBv())
            .patientCopayAmt(bv.getPatientCopayAmt())
            .deductibleMet(bv.getDeductibleMet())
            .oopMaxMet(bv.getOopMaxMet())
            .formularyTier(bv.getFormularyTier())
            .coverageNotes(bv.getCoverageNotes())
            .verifiedBy(bv.getVerifiedBy() != null ? bv.getVerifiedBy().getId() : null)
            .verifiedDate(bv.getVerifiedDate())
            .reverificationDueDate(bv.getReverificationDueDate())
            .build();
    }
}
