package com.hubaccess.domain.insurance.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BenefitsVerificationDto {
    private UUID id;
    private UUID caseId;
    private String status;
    private String benefitType;
    private Boolean coverageConfirmed;
    private Boolean paRequiredPerBv;
    private BigDecimal patientCopayAmt;
    private Boolean deductibleMet;
    private Boolean oopMaxMet;
    private Integer formularyTier;
    private String coverageNotes;
    private UUID verifiedBy;
    private LocalDate verifiedDate;
    private LocalDate reverificationDueDate;
}
