package com.hubaccess.domain.cases;

import com.hubaccess.domain.activity.Interaction;
import com.hubaccess.domain.activity.InteractionRepository;
import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.auth.HubUserRepository;
import com.hubaccess.domain.auth.UserProgramAssignmentRepository;
import com.hubaccess.domain.cases.dto.*;
import com.hubaccess.domain.patient.*;
import com.hubaccess.domain.patient.dto.PatientDto;
import com.hubaccess.domain.patient.dto.PrescriberDto;
import com.hubaccess.domain.program.Program;
import com.hubaccess.domain.program.ProgramRepository;
import com.hubaccess.util.CaseNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseStatusHistoryRepository statusHistoryRepository;
    private final PatientRepository patientRepository;
    private final PrescriberRepository prescriberRepository;
    private final ProgramRepository programRepository;
    private final HubUserRepository userRepository;
    private final UserProgramAssignmentRepository programAssignmentRepository;
    private final InteractionRepository interactionRepository;
    private final CaseNumberGenerator caseNumberGenerator;

    @Transactional(readOnly = true)
    public Page<CaseDto> getCases(Pageable pageable) {
        UUID userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();

        Page<HubCase> cases;
        if (isAdmin) {
            cases = caseRepository.findAll(pageable);
        } else {
            List<UUID> programIds = programAssignmentRepository.findActiveProgamIdsByUserId(userId);
            cases = caseRepository.findByProgramIdIn(programIds, pageable);
        }

        return cases.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CaseDto getCaseById(UUID id) {
        HubCase hubCase = caseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + id));
        return toDto(hubCase);
    }

    @Transactional
    public CaseDto createCase(CreateCaseRequest request) {
        Program program = programRepository.findById(request.getProgramId())
            .orElseThrow(() -> new EntityNotFoundException("Program not found: " + request.getProgramId()));

        // Create patient
        Patient patient = Patient.builder()
            .firstName(request.getPatientFirstName())
            .lastName(request.getPatientLastName())
            .dateOfBirth(request.getPatientDateOfBirth())
            .gender(request.getPatientGender())
            .phoneMobile(request.getPatientPhoneMobile())
            .email(request.getPatientEmail())
            .addressLine1(request.getPatientAddressLine1())
            .city(request.getPatientCity())
            .state(request.getPatientState())
            .zip(request.getPatientZip())
            .build();
        patient = patientRepository.save(patient);

        // Create prescriber if provided
        Prescriber prescriber = null;
        if (request.getPrescriberNpi() != null || request.getPrescriberFirstName() != null) {
            if (request.getPrescriberNpi() != null) {
                prescriber = prescriberRepository.findByNpi(request.getPrescriberNpi()).orElse(null);
            }
            if (prescriber == null) {
                prescriber = Prescriber.builder()
                    .npi(request.getPrescriberNpi())
                    .firstName(request.getPrescriberFirstName() != null ? request.getPrescriberFirstName() : "Unknown")
                    .lastName(request.getPrescriberLastName() != null ? request.getPrescriberLastName() : "Unknown")
                    .credential(request.getPrescriberCredential())
                    .build();
                prescriber = prescriberRepository.save(prescriber);
            }
        }

        String caseNumber = caseNumberGenerator.generate();

        HubUser currentUser = null;
        try {
            currentUser = userRepository.findById(getCurrentUserId()).orElse(null);
        } catch (Exception e) {
            log.debug("Could not resolve current user for case creation");
        }

        HubCase hubCase = HubCase.builder()
            .caseNumber(caseNumber)
            .program(program)
            .patient(patient)
            .prescriber(prescriber)
            .assignedCm(currentUser)
            .enrollmentSource(request.getEnrollmentSource())
            .priority(request.getPriority() != null ? request.getPriority() : "Normal")
            .notes(request.getNotes())
            .createdByUser(currentUser)
            .build();

        hubCase = caseRepository.save(hubCase);

        // Record initial status
        CaseStatusHistory history = CaseStatusHistory.builder()
            .hubCase(hubCase)
            .toStage("Referral")
            .toStatus("Active_Referral")
            .changedBy(currentUser)
            .changeReason("Case created")
            .build();
        statusHistoryRepository.save(history);

        return toDto(hubCase);
    }

    @Transactional
    public CaseDto updateCase(UUID id, UpdateCaseRequest request) {
        HubCase hubCase = caseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + id));

        String oldStage = hubCase.getStage();
        String oldStatus = hubCase.getStatus();

        if (request.getStage() != null) hubCase.setStage(request.getStage());
        if (request.getStatus() != null) hubCase.setStatus(request.getStatus());
        if (request.getConsentStatus() != null) {
            hubCase.setConsentStatus(request.getConsentStatus());
            if ("Received".equals(request.getConsentStatus())) {
                hubCase.setConsentReceivedAt(Instant.now());
            }
        }
        if (request.getMiStatus() != null) {
            hubCase.setMiStatus(request.getMiStatus());
            if ("Resolved".equals(request.getMiStatus())) {
                hubCase.setMiResolvedAt(Instant.now());
            }
        }
        if (request.getPriority() != null) hubCase.setPriority(request.getPriority());
        if (request.getSlaBreachFlag() != null) hubCase.setSlaBreachFlag(request.getSlaBreachFlag());
        if (request.getEscalationFlag() != null) hubCase.setEscalationFlag(request.getEscalationFlag());
        if (request.getClosedReason() != null) hubCase.setClosedReason(request.getClosedReason());
        if (request.getNotes() != null) hubCase.setNotes(request.getNotes());
        if (request.getAssignedCmId() != null) {
            HubUser cm = userRepository.findById(request.getAssignedCmId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getAssignedCmId()));
            hubCase.setAssignedCm(cm);
        }

        hubCase = caseRepository.save(hubCase);

        // Record status change if stage or status changed
        if (!oldStage.equals(hubCase.getStage()) || !oldStatus.equals(hubCase.getStatus())) {
            HubUser currentUser = null;
            try {
                currentUser = userRepository.findById(getCurrentUserId()).orElse(null);
            } catch (Exception e) {
                log.debug("Could not resolve current user for status history");
            }

            CaseStatusHistory history = CaseStatusHistory.builder()
                .hubCase(hubCase)
                .fromStage(oldStage)
                .toStage(hubCase.getStage())
                .fromStatus(oldStatus)
                .toStatus(hubCase.getStatus())
                .changedBy(currentUser)
                .changeReason(request.getChangeReason())
                .build();
            statusHistoryRepository.save(history);
        }

        return toDto(hubCase);
    }

    @Transactional(readOnly = true)
    public List<TimelineEntryDto> getTimeline(UUID caseId) {
        List<TimelineEntryDto> timeline = new ArrayList<>();

        // Add status history entries
        statusHistoryRepository.findByHubCaseIdOrderByChangedAtDesc(caseId).forEach(h -> {
            timeline.add(TimelineEntryDto.builder()
                .id(h.getId())
                .type("StatusChange")
                .summary(h.getFromStage() + " → " + h.getToStage())
                .details(h.getChangeReason())
                .performedBy(h.getChangedBy() != null ? h.getChangedBy().getFirstName() + " " + h.getChangedBy().getLastName() : "System")
                .timestamp(h.getChangedAt())
                .build());
        });

        // Add interactions
        interactionRepository.findByHubCaseIdOrderByInteractionAtDesc(caseId).forEach(i -> {
            timeline.add(TimelineEntryDto.builder()
                .id(i.getId())
                .type(i.getInteractionType())
                .summary(i.getSummary())
                .details(i.getNotes())
                .performedBy(i.getPerformedBy() != null ? i.getPerformedBy().getFirstName() + " " + i.getPerformedBy().getLastName() : "System")
                .timestamp(i.getInteractionAt())
                .build());
        });

        timeline.sort(Comparator.comparing(TimelineEntryDto::getTimestamp).reversed());
        return timeline;
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return UUID.fromString(auth.getName());
        }
        throw new IllegalStateException("No authenticated user");
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_HubAdmin"));
    }

    private CaseDto toDto(HubCase c) {
        PatientDto patientDto = null;
        if (c.getPatient() != null) {
            Patient p = c.getPatient();
            patientDto = PatientDto.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .dateOfBirth(p.getDateOfBirth())
                .gender(p.getGender())
                .phoneMobile(p.getPhoneMobile())
                .email(p.getEmail())
                .preferredLanguage(p.getPreferredLanguage())
                .preferredContactMethod(p.getPreferredContactMethod())
                .build();
        }

        PrescriberDto prescriberDto = null;
        if (c.getPrescriber() != null) {
            Prescriber pr = c.getPrescriber();
            prescriberDto = PrescriberDto.builder()
                .id(pr.getId())
                .npi(pr.getNpi())
                .firstName(pr.getFirstName())
                .lastName(pr.getLastName())
                .credential(pr.getCredential())
                .practiceName(pr.getPracticeName())
                .build();
        }

        return CaseDto.builder()
            .id(c.getId())
            .caseNumber(c.getCaseNumber())
            .programId(c.getProgram().getId())
            .programName(c.getProgram().getName())
            .drugBrandName(c.getProgram().getDrugBrandName())
            .manufacturerName(c.getProgram().getManufacturer().getName())
            .patient(patientDto)
            .prescriber(prescriberDto)
            .assignedCmId(c.getAssignedCm() != null ? c.getAssignedCm().getId() : null)
            .assignedCmName(c.getAssignedCm() != null ? c.getAssignedCm().getFirstName() + " " + c.getAssignedCm().getLastName() : null)
            .stage(c.getStage())
            .status(c.getStatus())
            .enrollmentSource(c.getEnrollmentSource())
            .consentStatus(c.getConsentStatus())
            .consentReceivedAt(c.getConsentReceivedAt())
            .miStatus(c.getMiStatus())
            .priority(c.getPriority())
            .slaBreachFlag(c.getSlaBreachFlag())
            .escalationFlag(c.getEscalationFlag())
            .closedReason(c.getClosedReason())
            .notes(c.getNotes())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }
}
