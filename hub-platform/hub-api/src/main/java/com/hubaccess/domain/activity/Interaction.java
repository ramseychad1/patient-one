package com.hubaccess.domain.activity;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "interaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private HubCase hubCase;

    @Column(name = "interaction_type", nullable = false, length = 30)
    private String interactionType;

    @Column(length = 10)
    private String direction;

    @Column(length = 20)
    private String channel;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "document_reference", length = 500)
    private String documentReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private HubUser performedBy;

    @Column(name = "interaction_at", nullable = false)
    private Instant interactionAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
