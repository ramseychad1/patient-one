package com.hubaccess.domain.outreach;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "patient_outreach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientOutreach {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private HubCase hubCase;

    @Column(name = "outreach_type", nullable = false, length = 20)
    private String outreachType;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String channel = "SMS";

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by")
    private HubUser sentBy;

    @Column(name = "unique_url", length = 500)
    private String uniqueUrl;

    @Column(name = "access_code", length = 20)
    private String accessCode;

    @Column(name = "url_expires_at")
    private Instant urlExpiresAt;

    @Column
    @Builder.Default
    private Boolean responded = false;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "response_channel", length = 10)
    private String responseChannel;

    @Column(name = "attempt_number")
    @Builder.Default
    private Integer attemptNumber = 1;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
