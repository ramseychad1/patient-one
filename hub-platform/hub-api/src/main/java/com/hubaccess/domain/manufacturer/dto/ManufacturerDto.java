package com.hubaccess.domain.manufacturer.dto;

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
public class ManufacturerDto {
    private UUID id;
    private String name;
    private String status;
    private String primaryContactName;
    private String primaryContactEmail;
    private String primaryContactPhone;
    private String contractReference;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
