package com.hubaccess.domain.cases.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCaseRequest {
    private String stage;
    private String status;
    private String consentStatus;
    private String miStatus;
    private String priority;
    private Boolean slaBreachFlag;
    private Boolean escalationFlag;
    private String closedReason;
    private String notes;
    private UUID assignedCmId;
    private String changeReason;
}
