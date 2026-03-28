package com.hubaccess.domain.program.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto {
    private UUID id;
    private UUID manufacturerId;
    private String manufacturerName;
    private String name;
    private String drugBrandName;
    private String drugGenericName;
    private String ndcCodes;
    private String therapeuticArea;
    private String status;
    private LocalDate programStartDate;
    private LocalDate programEndDate;
    private Instant createdAt;
}
