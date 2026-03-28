package com.hubaccess.domain.financial.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PriorAuthorizationDto {
    private UUID id;
    private UUID caseId;
    private String paNumber;
    private Integer attemptNumber;
    private UUID parentPaId;
    private String status;
    private LocalDate submittedDate;
    private String submissionMethod;
    private String payerId;
    private UUID submittedBy;
    private LocalDate decisionDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String denialReasonCode;
    private String denialReasonText;
    private LocalDate appealDeadline;
    private LocalDate slaSubmitDeadline;
    private Boolean slaBreached;
    private String clinicalNotes;
}
