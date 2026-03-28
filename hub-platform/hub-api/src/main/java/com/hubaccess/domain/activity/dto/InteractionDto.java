package com.hubaccess.domain.activity.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InteractionDto {
    private UUID id;
    private UUID caseId;
    private String interactionType;
    private String direction;
    private String channel;
    private String summary;
    private String notes;
    private String documentReference;
    private UUID performedBy;
    private String performedByName;
    private Instant interactionAt;
}
