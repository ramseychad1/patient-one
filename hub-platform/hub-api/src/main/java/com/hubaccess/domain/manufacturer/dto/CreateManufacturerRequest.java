package com.hubaccess.domain.manufacturer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateManufacturerRequest {
    @NotBlank
    private String name;
    private String primaryContactName;
    private String primaryContactEmail;
    private String primaryContactPhone;
    private String contractReference;
    private String notes;
}
