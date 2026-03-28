package com.hubaccess.domain.financial.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreatePaRequest {
    private String paNumber;
    private String submissionMethod;
    private String payerId;
    private LocalDate slaSubmitDeadline;
    private String clinicalNotes;
}
