package com.hubaccess.domain.cases.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateCaseRequest {
    @NotNull
    private UUID programId;

    // Patient fields
    @NotBlank
    private String patientFirstName;
    @NotBlank
    private String patientLastName;
    @NotNull
    private LocalDate patientDateOfBirth;
    private String patientGender;
    private String patientPhoneMobile;
    private String patientEmail;
    private String patientAddressLine1;
    private String patientCity;
    private String patientState;
    private String patientZip;

    // Prescriber (optional)
    private String prescriberNpi;
    private String prescriberFirstName;
    private String prescriberLastName;
    private String prescriberCredential;

    private String enrollmentSource;
    private String priority;
    private String notes;
}
