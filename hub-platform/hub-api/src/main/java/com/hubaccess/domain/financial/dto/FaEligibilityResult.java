package com.hubaccess.domain.financial.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FaEligibilityResult {
    private String faType;
    private String status;
    private BigDecimal fplPercentage;
    private String reason;
}
