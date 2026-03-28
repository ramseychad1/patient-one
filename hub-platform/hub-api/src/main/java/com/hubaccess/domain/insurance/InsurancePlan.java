package com.hubaccess.domain.insurance;

import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "insurance_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private HubCase hubCase;

    @Column(name = "plan_sequence", nullable = false, length = 10)
    @Builder.Default
    private String planSequence = "Primary";

    @Column(name = "insurance_type_code", nullable = false, length = 30)
    private String insuranceTypeCode;

    @Column(name = "payer_name", length = 200)
    private String payerName;

    @Column(name = "plan_name", length = 200)
    private String planName;

    @Column(name = "member_id", length = 100)
    private String memberId;

    @Column(name = "group_id", length = 100)
    private String groupId;

    @Column(name = "rx_bin", length = 20)
    private String rxBin;

    @Column(name = "rx_pcn", length = 20)
    private String rxPcn;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "term_date")
    private LocalDate termDate;

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
