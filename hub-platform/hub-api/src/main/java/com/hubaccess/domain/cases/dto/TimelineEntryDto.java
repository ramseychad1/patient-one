package com.hubaccess.domain.cases.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEntryDto {
    private UUID id;
    private String type;
    private String summary;
    private String details;
    private String performedBy;
    private Instant timestamp;
}
