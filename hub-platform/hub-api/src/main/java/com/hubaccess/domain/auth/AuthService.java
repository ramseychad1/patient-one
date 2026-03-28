package com.hubaccess.domain.auth;

import com.hubaccess.domain.auth.dto.AuthResponse;
import com.hubaccess.domain.auth.dto.LoginRequest;
import com.hubaccess.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final HubUserRepository userRepository;
    private final UserProgramAssignmentRepository programAssignmentRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        HubUser user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if ("Locked".equals(user.getStatus())) {
            throw new BadCredentialsException("Account is locked");
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getId().toString(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);
            if (user.getFailedLoginCount() >= 5) {
                user.setStatus("Locked");
                user.setLockedAt(Instant.now());
            }
            userRepository.save(user);
            throw e;
        }

        user.setFailedLoginCount(0);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        List<String> roles = user.getUserRoles().stream()
            .map(ur -> ur.getRole().getName())
            .toList();

        List<UUID> programIds = programAssignmentRepository.findActiveProgamIdsByUserId(user.getId());

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles, programIds);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        UUID userId = jwtService.extractUserId(refreshToken);
        HubUser user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<String> roles = user.getUserRoles().stream()
            .map(ur -> ur.getRole().getName())
            .toList();

        List<UUID> programIds = programAssignmentRepository.findActiveProgamIdsByUserId(user.getId());

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles, programIds);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .build();
    }
}
