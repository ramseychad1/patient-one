package com.hubaccess.domain.enrollment;

import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "enrollment_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    private HubCase hubCase;

    @Column(nullable = false, length = 10)
    private String source;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "portal_submission_id", length = 100)
    private String portalSubmissionId;

    @Column(name = "portal_submitted_by", length = 200)
    private String portalSubmittedBy;

    @Column(name = "erx_transaction_id", length = 100)
    private String erxTransactionId;

    @Column(name = "erx_prescriber_npi", length = 10)
    private String erxPrescriberNpi;

    @Column(name = "erx_drug_name", length = 200)
    private String erxDrugName;

    @Column(name = "erx_quantity", precision = 10, scale = 2)
    private BigDecimal erxQuantity;

    @Column(name = "erx_days_supply")
    private Integer erxDaysSupply;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "erx_raw_payload", columnDefinition = "jsonb")
    private String erxRawPayload;

    @Column(name = "mi_required")
    @Builder.Default
    private Boolean miRequired = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mi_missing_fields", columnDefinition = "jsonb")
    private String miMissingFields;

    @Column(name = "mi_triggered_at")
    private Instant miTriggeredAt;

    @Column(name = "mi_resolved_at")
    private Instant miResolvedAt;

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
