package com.hubaccess.domain.program;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "program_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, unique = true)
    private Program program;

    // Workflow flags
    @Column(name = "pa_required", nullable = false)
    @Builder.Default
    private Boolean paRequired = true;

    @Column(name = "adherence_program_enabled", nullable = false)
    @Builder.Default
    private Boolean adherenceProgramEnabled = true;

    @Column(name = "rems_tracking_enabled", nullable = false)
    @Builder.Default
    private Boolean remsTrackingEnabled = false;

    // Enrollment
    @Column(name = "enrollment_sources", nullable = false, length = 20)
    @Builder.Default
    private String enrollmentSources = "Both";

    @Column(name = "mi_required_for_erx", nullable = false)
    @Builder.Default
    private Boolean miRequiredForErx = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mi_required_fields", columnDefinition = "jsonb")
    private String miRequiredFields;

    // FA flags
    @Column(name = "copay_enabled", nullable = false)
    @Builder.Default
    private Boolean copayEnabled = false;

    @Column(name = "pap_enabled", nullable = false)
    @Builder.Default
    private Boolean papEnabled = false;

    @Column(name = "bridge_enabled", nullable = false)
    @Builder.Default
    private Boolean bridgeEnabled = false;

    // Copay rules
    @Column(name = "copay_income_limit_enabled")
    private Boolean copayIncomeLimitEnabled;

    @Column(name = "copay_income_limit_fpl_pct")
    private Integer copayIncomeLimitFplPct;

    @Column(name = "copay_monthly_cap_usd")
    private Integer copayMonthlyCapUsd;

    @Column(name = "copay_annual_cap_usd")
    private Integer copayAnnualCapUsd;

    @Column(name = "copay_enrollment_months")
    @Builder.Default
    private Integer copayEnrollmentMonths = 12;

    @Column(name = "copay_min_age")
    @Builder.Default
    private Integer copayMinAge = 18;

    // PAP rules
    @Column(name = "pap_fpl_threshold_pct")
    private Integer papFplThresholdPct;

    @Column(name = "pap_allow_commercial_insured")
    @Builder.Default
    private Boolean papAllowCommercialInsured = false;

    @Column(name = "pap_proof_of_income_required")
    @Builder.Default
    private Boolean papProofOfIncomeRequired = true;

    @Column(name = "pap_attestation_only")
    @Builder.Default
    private Boolean papAttestationOnly = false;

    @Column(name = "pap_supply_days")
    @Builder.Default
    private Integer papSupplyDays = 90;

    @Column(name = "pap_enrollment_months")
    @Builder.Default
    private Integer papEnrollmentMonths = 12;

    @Column(name = "pap_min_age")
    @Builder.Default
    private Integer papMinAge = 18;

    // Bridge rules
    @Column(name = "bridge_trigger_pa_pending")
    @Builder.Default
    private Boolean bridgeTriggerPaPending = true;

    @Column(name = "bridge_trigger_coverage_lapse")
    @Builder.Default
    private Boolean bridgeTriggerCoverageLapse = true;

    @Column(name = "bridge_trigger_new_enrollment")
    @Builder.Default
    private Boolean bridgeTriggerNewEnrollment = true;

    @Column(name = "bridge_supply_days")
    @Builder.Default
    private Integer bridgeSupplyDays = 30;

    @Column(name = "bridge_max_episodes_per_year")
    @Builder.Default
    private Integer bridgeMaxEpisodesPerYear = 1;

    @Column(name = "bridge_new_patient_only")
    @Builder.Default
    private Boolean bridgeNewPatientOnly = false;

    @Column(name = "bridge_income_limit_enabled")
    @Builder.Default
    private Boolean bridgeIncomeLimitEnabled = false;

    @Column(name = "bridge_income_limit_fpl_pct")
    private Integer bridgeIncomeLimitFplPct;

    // PA SLA
    @Column(name = "pa_sla_submit_days")
    @Builder.Default
    private Integer paSlaSumbitDays = 3;

    @Column(name = "pa_sla_followup_days")
    @Builder.Default
    private Integer paSlaFollowupDays = 5;

    @Column(name = "pa_appeal_window_days")
    @Builder.Default
    private Integer paAppealWindowDays = 30;

    @Column(name = "pa_auto_escalate_on_breach")
    @Builder.Default
    private Boolean paAutoEscalateOnBreach = true;

    // Consent
    @Column(name = "consent_url_expiry_days")
    @Builder.Default
    private Integer consentUrlExpiryDays = 7;

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
