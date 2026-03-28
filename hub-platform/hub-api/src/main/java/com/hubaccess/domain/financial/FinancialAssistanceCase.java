package com.hubaccess.domain.financial;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "financial_assist_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialAssistanceCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private HubCase hubCase;

    @Column(name = "fa_type", nullable = false, length = 10)
    private String faType;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Evaluating";

    @Column(name = "annual_household_income", precision = 12, scale = 2)
    private BigDecimal annualHouseholdIncome;

    @Column(name = "household_size")
    private Integer householdSize;

    @Column(name = "fpl_percentage_calculated", precision = 6, scale = 2)
    private BigDecimal fplPercentageCalculated;

    @Column(name = "income_verified")
    @Builder.Default
    private Boolean incomeVerified = false;

    @Column(name = "income_verification_method", length = 20)
    private String incomeVerificationMethod;

    @Column(name = "eligibility_determined_at")
    private Instant eligibilityDeterminedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_determined_by")
    private HubUser eligibilityDeterminedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "benefit_amount_monthly", precision = 10, scale = 2)
    private BigDecimal benefitAmountMonthly;

    @Column(name = "benefit_ytd_used", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal benefitYtdUsed = BigDecimal.ZERO;

    @Column(name = "bridge_supply_days_authorized")
    private Integer bridgeSupplyDaysAuthorized;

    @Column(name = "bridge_ship_date")
    private LocalDate bridgeShipDate;

    @Column(name = "denial_reason", columnDefinition = "TEXT")
    private String denialReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
