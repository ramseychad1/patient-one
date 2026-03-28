package com.hubaccess.domain.insurance;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "benefits_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitsVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private HubCase hubCase;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Pending";

    @Column(name = "benefit_type", length = 20)
    private String benefitType;

    @Column(name = "coverage_confirmed")
    private Boolean coverageConfirmed;

    @Column(name = "pa_required_per_bv")
    private Boolean paRequiredPerBv;

    @Column(name = "patient_copay_amt", precision = 10, scale = 2)
    private BigDecimal patientCopayAmt;

    @Column(name = "deductible_met")
    private Boolean deductibleMet;

    @Column(name = "oop_max_met")
    private Boolean oopMaxMet;

    @Column(name = "formulary_tier")
    private Integer formularyTier;

    @Column(name = "coverage_notes", columnDefinition = "TEXT")
    private String coverageNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private HubUser verifiedBy;

    @Column(name = "verified_date")
    private LocalDate verifiedDate;

    @Column(name = "reverification_due_date")
    private LocalDate reverificationDueDate;

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
