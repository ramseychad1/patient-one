package com.hubaccess.domain.auth.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String status;
    private UUID manufacturerId;
}
