package com.hubaccess.domain.enrollment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EnrollmentRecordDto {
    private UUID id;
    private UUID caseId;
    private String source;
    private Instant receivedAt;
    private String portalSubmissionId;
    private String portalSubmittedBy;
    private String erxTransactionId;
    private String erxPrescriberNpi;
    private String erxDrugName;
    private BigDecimal erxQuantity;
    private Integer erxDaysSupply;
    private Boolean miRequired;
    private String miMissingFields;
    private Instant miTriggeredAt;
    private Instant miResolvedAt;
}
