package com.hubaccess.domain.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignProgramsRequest {
    @NotEmpty
    private List<UUID> programIds;
    private String accessLevel = "ReadWrite";
}
