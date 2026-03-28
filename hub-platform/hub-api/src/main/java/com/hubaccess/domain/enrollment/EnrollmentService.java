package com.hubaccess.domain.enrollment;

import com.hubaccess.domain.enrollment.dto.EnrollmentRecordDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public EnrollmentRecordDto getByCase(UUID caseId) {
        EnrollmentRecord record = enrollmentRepository.findByHubCaseId(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found for case: " + caseId));
        return toDto(record);
    }

    private EnrollmentRecordDto toDto(EnrollmentRecord r) {
        return EnrollmentRecordDto.builder()
            .id(r.getId()).caseId(r.getHubCase().getId())
            .source(r.getSource()).receivedAt(r.getReceivedAt())
            .portalSubmissionId(r.getPortalSubmissionId())
            .portalSubmittedBy(r.getPortalSubmittedBy())
            .erxTransactionId(r.getErxTransactionId())
            .erxPrescriberNpi(r.getErxPrescriberNpi())
            .erxDrugName(r.getErxDrugName())
            .erxQuantity(r.getErxQuantity())
            .erxDaysSupply(r.getErxDaysSupply())
            .miRequired(r.getMiRequired())
            .miMissingFields(r.getMiMissingFields())
            .miTriggeredAt(r.getMiTriggeredAt())
            .miResolvedAt(r.getMiResolvedAt())
            .build();
    }
}
