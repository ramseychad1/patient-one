package com.hubaccess.domain.activity.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UpdateTaskRequest {
    private String status;
    private String priority;
    private String notes;
    private UUID assignedTo;
}
