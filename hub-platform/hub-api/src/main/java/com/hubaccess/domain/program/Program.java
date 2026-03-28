package com.hubaccess.domain.program;

import com.hubaccess.domain.manufacturer.Manufacturer;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "drug_brand_name", nullable = false, length = 200)
    private String drugBrandName;

    @Column(name = "drug_generic_name", length = 200)
    private String drugGenericName;

    @Column(name = "ndc_codes", columnDefinition = "TEXT")
    private String ndcCodes;

    @Column(name = "therapeutic_area", length = 100)
    private String therapeuticArea;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Active";

    @Column(name = "program_start_date")
    private LocalDate programStartDate;

    @Column(name = "program_end_date")
    private LocalDate programEndDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @Column(name = "created_by")
    private UUID createdBy;

    @OneToOne(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProgramConfig programConfig;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
