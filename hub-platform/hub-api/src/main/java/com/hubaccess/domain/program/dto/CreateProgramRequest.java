package com.hubaccess.domain.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateProgramRequest {
    @NotNull
    private UUID manufacturerId;
    @NotBlank
    private String name;
    @NotBlank
    private String drugBrandName;
    private String drugGenericName;
    private String ndcCodes;
    private String therapeuticArea;
    private String status;
    private LocalDate programStartDate;
    private LocalDate programEndDate;
}
