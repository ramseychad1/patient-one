package com.hubaccess.domain.manufacturer;

import com.hubaccess.domain.manufacturer.dto.CreateManufacturerRequest;
import com.hubaccess.domain.manufacturer.dto.ManufacturerDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public List<ManufacturerDto> getAll() {
        return manufacturerRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ManufacturerDto getById(UUID id) {
        return toDto(manufacturerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found: " + id)));
    }

    @Transactional
    public ManufacturerDto create(CreateManufacturerRequest request) {
        if (manufacturerRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Manufacturer already exists: " + request.getName());
        }
        Manufacturer manufacturer = Manufacturer.builder()
            .name(request.getName())
            .primaryContactName(request.getPrimaryContactName())
            .primaryContactEmail(request.getPrimaryContactEmail())
            .primaryContactPhone(request.getPrimaryContactPhone())
            .contractReference(request.getContractReference())
            .notes(request.getNotes())
            .build();
        return toDto(manufacturerRepository.save(manufacturer));
    }

    @Transactional
    public ManufacturerDto update(UUID id, CreateManufacturerRequest request) {
        Manufacturer manufacturer = manufacturerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found: " + id));
        if (request.getName() != null) manufacturer.setName(request.getName());
        if (request.getPrimaryContactName() != null) manufacturer.setPrimaryContactName(request.getPrimaryContactName());
        if (request.getPrimaryContactEmail() != null) manufacturer.setPrimaryContactEmail(request.getPrimaryContactEmail());
        if (request.getPrimaryContactPhone() != null) manufacturer.setPrimaryContactPhone(request.getPrimaryContactPhone());
        if (request.getContractReference() != null) manufacturer.setContractReference(request.getContractReference());
        if (request.getNotes() != null) manufacturer.setNotes(request.getNotes());
        return toDto(manufacturerRepository.save(manufacturer));
    }

    private ManufacturerDto toDto(Manufacturer m) {
        return ManufacturerDto.builder()
            .id(m.getId())
            .name(m.getName())
            .status(m.getStatus())
            .primaryContactName(m.getPrimaryContactName())
            .primaryContactEmail(m.getPrimaryContactEmail())
            .primaryContactPhone(m.getPrimaryContactPhone())
            .contractReference(m.getContractReference())
            .notes(m.getNotes())
            .createdAt(m.getCreatedAt())
            .updatedAt(m.getUpdatedAt())
            .build();
    }
}
