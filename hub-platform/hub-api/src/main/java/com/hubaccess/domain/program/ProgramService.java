package com.hubaccess.domain.program;

import com.hubaccess.domain.manufacturer.Manufacturer;
import com.hubaccess.domain.manufacturer.ManufacturerRepository;
import com.hubaccess.domain.program.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramConfigRepository configRepository;
    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public List<ProgramDto> getAll() {
        return programRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProgramDto getById(UUID id) {
        return toDto(programRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Program not found: " + id)));
    }

    @Transactional
    public ProgramDto create(CreateProgramRequest request) {
        Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
            .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found: " + request.getManufacturerId()));

        Program program = Program.builder()
            .manufacturer(manufacturer)
            .name(request.getName())
            .drugBrandName(request.getDrugBrandName())
            .drugGenericName(request.getDrugGenericName())
            .ndcCodes(request.getNdcCodes())
            .therapeuticArea(request.getTherapeuticArea())
            .status(request.getStatus() != null ? request.getStatus() : "Active")
            .programStartDate(request.getProgramStartDate())
            .programEndDate(request.getProgramEndDate())
            .build();

        program = programRepository.save(program);

        // Auto-create default config
        ProgramConfig config = ProgramConfig.builder()
            .program(program)
            .build();
        configRepository.save(config);

        return toDto(program);
    }

    @Transactional
    public ProgramDto update(UUID id, CreateProgramRequest request) {
        Program program = programRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Program not found: " + id));

        if (request.getName() != null) program.setName(request.getName());
        if (request.getDrugBrandName() != null) program.setDrugBrandName(request.getDrugBrandName());
        if (request.getDrugGenericName() != null) program.setDrugGenericName(request.getDrugGenericName());
        if (request.getNdcCodes() != null) program.setNdcCodes(request.getNdcCodes());
        if (request.getTherapeuticArea() != null) program.setTherapeuticArea(request.getTherapeuticArea());
        if (request.getStatus() != null) program.setStatus(request.getStatus());
        if (request.getProgramStartDate() != null) program.setProgramStartDate(request.getProgramStartDate());
        if (request.getProgramEndDate() != null) program.setProgramEndDate(request.getProgramEndDate());

        return toDto(programRepository.save(program));
    }

    @Transactional(readOnly = true)
    public ProgramConfigDto getConfig(UUID programId) {
        ProgramConfig config = configRepository.findByProgramId(programId)
            .orElseThrow(() -> new EntityNotFoundException("Config not found for program: " + programId));
        return toConfigDto(config);
    }

    @Transactional
    public ProgramConfigDto updateConfig(UUID programId, UpdateProgramConfigRequest request) {
        ProgramConfig config = configRepository.findByProgramId(programId)
            .orElseThrow(() -> new EntityNotFoundException("Config not found for program: " + programId));

        if (request.getPaRequired() != null) config.setPaRequired(request.getPaRequired());
        if (request.getAdherenceProgramEnabled() != null) config.setAdherenceProgramEnabled(request.getAdherenceProgramEnabled());
        if (request.getRemsTrackingEnabled() != null) config.setRemsTrackingEnabled(request.getRemsTrackingEnabled());
        if (request.getEnrollmentSources() != null) config.setEnrollmentSources(request.getEnrollmentSources());
        if (request.getMiRequiredForErx() != null) config.setMiRequiredForErx(request.getMiRequiredForErx());
        if (request.getMiRequiredFields() != null) config.setMiRequiredFields(request.getMiRequiredFields());
        if (request.getCopayEnabled() != null) config.setCopayEnabled(request.getCopayEnabled());
        if (request.getPapEnabled() != null) config.setPapEnabled(request.getPapEnabled());
        if (request.getBridgeEnabled() != null) config.setBridgeEnabled(request.getBridgeEnabled());
        if (request.getCopayIncomeLimitEnabled() != null) config.setCopayIncomeLimitEnabled(request.getCopayIncomeLimitEnabled());
        if (request.getCopayIncomeLimitFplPct() != null) config.setCopayIncomeLimitFplPct(request.getCopayIncomeLimitFplPct());
        if (request.getCopayMonthlyCapUsd() != null) config.setCopayMonthlyCapUsd(request.getCopayMonthlyCapUsd());
        if (request.getCopayAnnualCapUsd() != null) config.setCopayAnnualCapUsd(request.getCopayAnnualCapUsd());
        if (request.getCopayEnrollmentMonths() != null) config.setCopayEnrollmentMonths(request.getCopayEnrollmentMonths());
        if (request.getCopayMinAge() != null) config.setCopayMinAge(request.getCopayMinAge());
        if (request.getPapFplThresholdPct() != null) config.setPapFplThresholdPct(request.getPapFplThresholdPct());
        if (request.getPapAllowCommercialInsured() != null) config.setPapAllowCommercialInsured(request.getPapAllowCommercialInsured());
        if (request.getPapProofOfIncomeRequired() != null) config.setPapProofOfIncomeRequired(request.getPapProofOfIncomeRequired());
        if (request.getPapAttestationOnly() != null) config.setPapAttestationOnly(request.getPapAttestationOnly());
        if (request.getPapSupplyDays() != null) config.setPapSupplyDays(request.getPapSupplyDays());
        if (request.getPapEnrollmentMonths() != null) config.setPapEnrollmentMonths(request.getPapEnrollmentMonths());
        if (request.getPapMinAge() != null) config.setPapMinAge(request.getPapMinAge());
        if (request.getBridgeTriggerPaPending() != null) config.setBridgeTriggerPaPending(request.getBridgeTriggerPaPending());
        if (request.getBridgeTriggerCoverageLapse() != null) config.setBridgeTriggerCoverageLapse(request.getBridgeTriggerCoverageLapse());
        if (request.getBridgeTriggerNewEnrollment() != null) config.setBridgeTriggerNewEnrollment(request.getBridgeTriggerNewEnrollment());
        if (request.getBridgeSupplyDays() != null) config.setBridgeSupplyDays(request.getBridgeSupplyDays());
        if (request.getBridgeMaxEpisodesPerYear() != null) config.setBridgeMaxEpisodesPerYear(request.getBridgeMaxEpisodesPerYear());
        if (request.getBridgeNewPatientOnly() != null) config.setBridgeNewPatientOnly(request.getBridgeNewPatientOnly());
        if (request.getBridgeIncomeLimitEnabled() != null) config.setBridgeIncomeLimitEnabled(request.getBridgeIncomeLimitEnabled());
        if (request.getBridgeIncomeLimitFplPct() != null) config.setBridgeIncomeLimitFplPct(request.getBridgeIncomeLimitFplPct());
        if (request.getPaSlaSumbitDays() != null) config.setPaSlaSumbitDays(request.getPaSlaSumbitDays());
        if (request.getPaSlaFollowupDays() != null) config.setPaSlaFollowupDays(request.getPaSlaFollowupDays());
        if (request.getPaAppealWindowDays() != null) config.setPaAppealWindowDays(request.getPaAppealWindowDays());
        if (request.getPaAutoEscalateOnBreach() != null) config.setPaAutoEscalateOnBreach(request.getPaAutoEscalateOnBreach());
        if (request.getConsentUrlExpiryDays() != null) config.setConsentUrlExpiryDays(request.getConsentUrlExpiryDays());

        return toConfigDto(configRepository.save(config));
    }

    private ProgramDto toDto(Program p) {
        return ProgramDto.builder()
            .id(p.getId())
            .manufacturerId(p.getManufacturer().getId())
            .manufacturerName(p.getManufacturer().getName())
            .name(p.getName())
            .drugBrandName(p.getDrugBrandName())
            .drugGenericName(p.getDrugGenericName())
            .ndcCodes(p.getNdcCodes())
            .therapeuticArea(p.getTherapeuticArea())
            .status(p.getStatus())
            .programStartDate(p.getProgramStartDate())
            .programEndDate(p.getProgramEndDate())
            .createdAt(p.getCreatedAt())
            .build();
    }

    private ProgramConfigDto toConfigDto(ProgramConfig c) {
        return ProgramConfigDto.builder()
            .id(c.getId())
            .programId(c.getProgram().getId())
            .paRequired(c.getPaRequired())
            .adherenceProgramEnabled(c.getAdherenceProgramEnabled())
            .remsTrackingEnabled(c.getRemsTrackingEnabled())
            .enrollmentSources(c.getEnrollmentSources())
            .miRequiredForErx(c.getMiRequiredForErx())
            .miRequiredFields(c.getMiRequiredFields())
            .copayEnabled(c.getCopayEnabled())
            .papEnabled(c.getPapEnabled())
            .bridgeEnabled(c.getBridgeEnabled())
            .copayIncomeLimitEnabled(c.getCopayIncomeLimitEnabled())
            .copayIncomeLimitFplPct(c.getCopayIncomeLimitFplPct())
            .copayMonthlyCapUsd(c.getCopayMonthlyCapUsd())
            .copayAnnualCapUsd(c.getCopayAnnualCapUsd())
            .copayEnrollmentMonths(c.getCopayEnrollmentMonths())
            .copayMinAge(c.getCopayMinAge())
            .papFplThresholdPct(c.getPapFplThresholdPct())
            .papAllowCommercialInsured(c.getPapAllowCommercialInsured())
            .papProofOfIncomeRequired(c.getPapProofOfIncomeRequired())
            .papAttestationOnly(c.getPapAttestationOnly())
            .papSupplyDays(c.getPapSupplyDays())
            .papEnrollmentMonths(c.getPapEnrollmentMonths())
            .papMinAge(c.getPapMinAge())
            .bridgeTriggerPaPending(c.getBridgeTriggerPaPending())
            .bridgeTriggerCoverageLapse(c.getBridgeTriggerCoverageLapse())
            .bridgeTriggerNewEnrollment(c.getBridgeTriggerNewEnrollment())
            .bridgeSupplyDays(c.getBridgeSupplyDays())
            .bridgeMaxEpisodesPerYear(c.getBridgeMaxEpisodesPerYear())
            .bridgeNewPatientOnly(c.getBridgeNewPatientOnly())
            .bridgeIncomeLimitEnabled(c.getBridgeIncomeLimitEnabled())
            .bridgeIncomeLimitFplPct(c.getBridgeIncomeLimitFplPct())
            .paSlaSumbitDays(c.getPaSlaSumbitDays())
            .paSlaFollowupDays(c.getPaSlaFollowupDays())
            .paAppealWindowDays(c.getPaAppealWindowDays())
            .paAutoEscalateOnBreach(c.getPaAutoEscalateOnBreach())
            .consentUrlExpiryDays(c.getConsentUrlExpiryDays())
            .build();
    }
}
