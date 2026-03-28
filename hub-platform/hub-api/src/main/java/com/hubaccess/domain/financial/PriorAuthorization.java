package com.hubaccess.domain.financial;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "prior_authorization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriorAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private HubCase hubCase;

    @Column(name = "pa_number", length = 100)
    private String paNumber;

    @Column(name = "attempt_number", nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pa_id")
    private PriorAuthorization parentPa;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Draft";

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "submission_method", length = 20)
    private String submissionMethod;

    @Column(name = "payer_id", length = 100)
    private String payerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private HubUser submittedBy;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "denial_reason_code", length = 50)
    private String denialReasonCode;

    @Column(name = "denial_reason_text", columnDefinition = "TEXT")
    private String denialReasonText;

    @Column(name = "appeal_deadline")
    private LocalDate appealDeadline;

    @Column(name = "sla_submit_deadline")
    private LocalDate slaSubmitDeadline;

    @Column(name = "sla_breached")
    @Builder.Default
    private Boolean slaBreached = false;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

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
