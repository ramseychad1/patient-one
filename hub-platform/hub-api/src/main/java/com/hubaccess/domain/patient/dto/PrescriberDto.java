package com.hubaccess.domain.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriberDto {
    private UUID id;
    private String npi;
    private String firstName;
    private String lastName;
    private String credential;
    private String practiceName;
    private String phone;
    private String fax;
    private String addressLine1;
    private String city;
    private String state;
    private String zip;
}
