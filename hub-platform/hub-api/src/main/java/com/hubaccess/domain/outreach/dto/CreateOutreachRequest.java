package com.hubaccess.domain.outreach.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOutreachRequest {
    @NotBlank
    private String outreachType;
    @NotBlank
    private String phoneNumber;
    private String messageBody;
}
