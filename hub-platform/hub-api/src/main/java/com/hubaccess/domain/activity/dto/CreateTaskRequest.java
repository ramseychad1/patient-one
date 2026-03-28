package com.hubaccess.domain.activity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String taskType;
    @NotBlank
    private String title;
    private String priority = "Normal";
    private LocalDate dueDate;
    private UUID assignedTo;
    private String notes;
}
