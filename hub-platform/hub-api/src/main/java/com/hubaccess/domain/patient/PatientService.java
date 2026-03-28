package com.hubaccess.domain.patient;

import com.hubaccess.domain.patient.dto.PatientDto;
import com.hubaccess.domain.patient.dto.PrescriberDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PrescriberRepository prescriberRepository;

    @Transactional(readOnly = true)
    public PatientDto getPatientById(UUID id) {
        return toDto(patientRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Patient not found: " + id)));
    }

    @Transactional(readOnly = true)
    public PrescriberDto getPrescriberById(UUID id) {
        return toPrescriberDto(prescriberRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Prescriber not found: " + id)));
    }

    private PatientDto toDto(Patient p) {
        return PatientDto.builder()
            .id(p.getId())
            .firstName(p.getFirstName())
            .lastName(p.getLastName())
            .dateOfBirth(p.getDateOfBirth())
            .gender(p.getGender())
            .phoneMobile(p.getPhoneMobile())
            .phoneHome(p.getPhoneHome())
            .email(p.getEmail())
            .addressLine1(p.getAddressLine1())
            .addressLine2(p.getAddressLine2())
            .city(p.getCity())
            .state(p.getState())
            .zip(p.getZip())
            .preferredLanguage(p.getPreferredLanguage())
            .preferredContactMethod(p.getPreferredContactMethod())
            .createdAt(p.getCreatedAt())
            .build();
    }

    private PrescriberDto toPrescriberDto(Prescriber p) {
        return PrescriberDto.builder()
            .id(p.getId())
            .npi(p.getNpi())
            .firstName(p.getFirstName())
            .lastName(p.getLastName())
            .credential(p.getCredential())
            .practiceName(p.getPracticeName())
            .phone(p.getPhone())
            .fax(p.getFax())
            .addressLine1(p.getAddressLine1())
            .city(p.getCity())
            .state(p.getState())
            .zip(p.getZip())
            .build();
    }
}
