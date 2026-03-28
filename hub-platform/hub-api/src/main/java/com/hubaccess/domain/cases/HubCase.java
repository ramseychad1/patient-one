package com.hubaccess.domain.cases;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.patient.Patient;
import com.hubaccess.domain.patient.Prescriber;
import com.hubaccess.domain.program.Program;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hub_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "case_number", unique = true, nullable = false, length = 20)
    private String caseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescriber_id")
    private Prescriber prescriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_cm_id")
    private HubUser assignedCm;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String stage = "Referral";

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "Active_Referral";

    @Column(name = "enrollment_source", length = 10)
    private String enrollmentSource;

    @Column(name = "consent_status", nullable = false, length = 20)
    @Builder.Default
    private String consentStatus = "Pending";

    @Column(name = "consent_received_at")
    private Instant consentReceivedAt;

    @Column(name = "mi_status", length = 20)
    private String miStatus;

    @Column(name = "mi_resolved_at")
    private Instant miResolvedAt;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String priority = "Normal";

    @Column(name = "sla_breach_flag", nullable = false)
    @Builder.Default
    private Boolean slaBreachFlag = false;

    @Column(name = "escalation_flag", nullable = false)
    @Builder.Default
    private Boolean escalationFlag = false;

    @Column(name = "closed_reason", length = 200)
    private String closedReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private HubUser createdByUser;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
