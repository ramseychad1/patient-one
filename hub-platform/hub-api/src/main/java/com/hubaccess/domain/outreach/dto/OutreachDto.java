package com.hubaccess.domain.outreach.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OutreachDto {
    private UUID id;
    private UUID caseId;
    private String outreachType;
    private String channel;
    private String phoneNumber;
    private Instant sentAt;
    private UUID sentBy;
    private String uniqueUrl;
    private String accessCode;
    private Instant urlExpiresAt;
    private Boolean responded;
    private Instant respondedAt;
    private String responseChannel;
    private Integer attemptNumber;
    private String messageBody;
}
