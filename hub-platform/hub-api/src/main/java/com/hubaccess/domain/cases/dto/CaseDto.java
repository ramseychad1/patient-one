package com.hubaccess.domain.cases.dto;

import com.hubaccess.domain.patient.dto.PatientDto;
import com.hubaccess.domain.patient.dto.PrescriberDto;
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
public class CaseDto {
    private UUID id;
    private String caseNumber;
    private UUID programId;
    private String programName;
    private String drugBrandName;
    private String manufacturerName;
    private PatientDto patient;
    private PrescriberDto prescriber;
    private UUID assignedCmId;
    private String assignedCmName;
    private String stage;
    private String status;
    private String enrollmentSource;
    private String consentStatus;
    private Instant consentReceivedAt;
    private String miStatus;
    private String priority;
    private Boolean slaBreachFlag;
    private Boolean escalationFlag;
    private String closedReason;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
