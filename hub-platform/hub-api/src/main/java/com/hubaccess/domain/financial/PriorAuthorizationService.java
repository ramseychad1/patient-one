package com.hubaccess.domain.financial;

import com.hubaccess.domain.cases.CaseRepository;
import com.hubaccess.domain.cases.HubCase;
import com.hubaccess.domain.financial.dto.CreatePaRequest;
import com.hubaccess.domain.financial.dto.PriorAuthorizationDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriorAuthorizationService {

    private final PriorAuthorizationRepository paRepository;
    private final CaseRepository caseRepository;

    @Transactional(readOnly = true)
    public List<PriorAuthorizationDto> getByCaseId(UUID caseId) {
        return paRepository.findByHubCaseId(caseId).stream().map(this::toDto).toList();
    }

    @Transactional
    public PriorAuthorizationDto create(UUID caseId, CreatePaRequest request) {
        HubCase hubCase = caseRepository.findById(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        PriorAuthorization pa = PriorAuthorization.builder()
            .hubCase(hubCase)
            .paNumber(request.getPaNumber())
            .submissionMethod(request.getSubmissionMethod())
            .payerId(request.getPayerId())
            .slaSubmitDeadline(request.getSlaSubmitDeadline())
            .clinicalNotes(request.getClinicalNotes())
            .build();

        return toDto(paRepository.save(pa));
    }

    @Transactional
    public PriorAuthorizationDto update(UUID paId, PriorAuthorizationDto request) {
        PriorAuthorization pa = paRepository.findById(paId)
            .orElseThrow(() -> new EntityNotFoundException("PA not found: " + paId));
        if (request.getStatus() != null) pa.setStatus(request.getStatus());
        if (request.getSubmittedDate() != null) pa.setSubmittedDate(request.getSubmittedDate());
        if (request.getDecisionDate() != null) pa.setDecisionDate(request.getDecisionDate());
        if (request.getDenialReasonCode() != null) pa.setDenialReasonCode(request.getDenialReasonCode());
        if (request.getDenialReasonText() != null) pa.setDenialReasonText(request.getDenialReasonText());
        if (request.getClinicalNotes() != null) pa.setClinicalNotes(request.getClinicalNotes());
        return toDto(paRepository.save(pa));
    }

    private PriorAuthorizationDto toDto(PriorAuthorization pa) {
        return PriorAuthorizationDto.builder()
            .id(pa.getId()).caseId(pa.getHubCase().getId())
            .paNumber(pa.getPaNumber()).attemptNumber(pa.getAttemptNumber())
            .parentPaId(pa.getParentPa() != null ? pa.getParentPa().getId() : null)
            .status(pa.getStatus()).submittedDate(pa.getSubmittedDate())
            .submissionMethod(pa.getSubmissionMethod()).payerId(pa.getPayerId())
            .submittedBy(pa.getSubmittedBy() != null ? pa.getSubmittedBy().getId() : null)
            .decisionDate(pa.getDecisionDate()).effectiveDate(pa.getEffectiveDate())
            .expiryDate(pa.getExpiryDate()).denialReasonCode(pa.getDenialReasonCode())
            .denialReasonText(pa.getDenialReasonText()).appealDeadline(pa.getAppealDeadline())
            .slaSubmitDeadline(pa.getSlaSubmitDeadline()).slaBreached(pa.getSlaBreached())
            .clinicalNotes(pa.getClinicalNotes())
            .build();
    }
}
