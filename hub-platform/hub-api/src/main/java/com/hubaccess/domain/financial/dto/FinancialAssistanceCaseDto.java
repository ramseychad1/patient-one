package com.hubaccess.domain.financial.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FinancialAssistanceCaseDto {
    private UUID id;
    private UUID caseId;
    private String faType;
    private String status;
    private BigDecimal annualHouseholdIncome;
    private Integer householdSize;
    private BigDecimal fplPercentageCalculated;
    private Boolean incomeVerified;
    private String incomeVerificationMethod;
    private Instant eligibilityDeterminedAt;
    private UUID eligibilityDeterminedBy;
    private LocalDate approvalDate;
    private LocalDate expiryDate;
    private BigDecimal benefitAmountMonthly;
    private BigDecimal benefitYtdUsed;
    private Integer bridgeSupplyDaysAuthorized;
    private LocalDate bridgeShipDate;
    private String denialReason;
    private String notes;
}
