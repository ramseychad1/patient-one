package com.hubaccess.domain.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateInsurancePlanRequest {
    private String planSequence = "Primary";
    @NotBlank
    private String insuranceTypeCode;
    private String payerName;
    private String planName;
    private String memberId;
    private String groupId;
    private String rxBin;
    private String rxPcn;
    private LocalDate effectiveDate;
    private LocalDate termDate;
}
