package com.hubaccess.domain.auth;

import com.hubaccess.domain.auth.dto.*;
import com.hubaccess.domain.manufacturer.Manufacturer;
import com.hubaccess.domain.manufacturer.ManufacturerRepository;
import com.hubaccess.domain.program.Program;
import com.hubaccess.domain.program.ProgramRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final HubUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserProgramAssignmentRepository programAssignmentRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ProgramRepository programRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<HubUserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public HubUserDto getUserById(UUID id) {
        return toDto(userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)));
    }

    @Transactional
    public HubUserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        HubUser user = HubUser.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();

        if (request.getManufacturerId() != null) {
            Manufacturer mfr = manufacturerRepository.findById(request.getManufacturerId())
                .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found"));
            user.setManufacturer(mfr);
        }

        user = userRepository.save(user);

        if (request.getRoleName() != null) {
            Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + request.getRoleName()));
            UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
            userRoleRepository.save(userRole);
        }

        // Re-fetch to get roles loaded
        user = userRepository.findById(user.getId()).orElseThrow();
        return toDto(user);
    }

    @Transactional
    public HubUserDto updateUser(UUID id, UpdateUserRequest request) {
        HubUser user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getManufacturerId() != null) {
            Manufacturer mfr = manufacturerRepository.findById(request.getManufacturerId())
                .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found"));
            user.setManufacturer(mfr);
        }

        return toDto(userRepository.save(user));
    }

    @Transactional
    public void assignPrograms(UUID userId, AssignProgramsRequest request) {
        HubUser user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        for (UUID programId : request.getProgramIds()) {
            Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found: " + programId));

            UserProgramAssignment assignment = UserProgramAssignment.builder()
                .user(user)
                .program(program)
                .accessLevel(request.getAccessLevel())
                .build();
            programAssignmentRepository.save(assignment);
        }
    }

    private HubUserDto toDto(HubUser user) {
        List<String> roles = user.getUserRoles().stream()
            .map(ur -> ur.getRole().getName())
            .toList();
        List<UUID> programIds = programAssignmentRepository.findActiveProgamIdsByUserId(user.getId());

        return HubUserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .status(user.getStatus())
            .manufacturerId(user.getManufacturer() != null ? user.getManufacturer().getId() : null)
            .lastLoginAt(user.getLastLoginAt())
            .roles(roles)
            .programIds(programIds)
            .createdAt(user.getCreatedAt())
            .build();
    }
}
