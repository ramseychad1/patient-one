package com.hubaccess.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubUserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private UUID manufacturerId;
    private Instant lastLoginAt;
    private List<String> roles;
    private List<UUID> programIds;
    private Instant createdAt;
}
