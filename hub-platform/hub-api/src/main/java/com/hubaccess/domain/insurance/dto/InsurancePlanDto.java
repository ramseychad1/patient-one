package com.hubaccess.domain.insurance.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InsurancePlanDto {
    private UUID id;
    private UUID caseId;
    private String planSequence;
    private String insuranceTypeCode;
    private String payerName;
    private String planName;
    private String memberId;
    private String groupId;
    private String rxBin;
    private String rxPcn;
    private LocalDate effectiveDate;
    private LocalDate termDate;
}
