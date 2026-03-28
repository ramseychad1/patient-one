package com.hubaccess.domain.patient;

import com.hubaccess.domain.patient.dto.PatientDto;
import com.hubaccess.domain.patient.dto.PrescriberDto;
import com.hubaccess.web.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/patients/{id}")
    public ResponseEntity<ApiResponse<PatientDto>> getPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatientById(id)));
    }

    @GetMapping("/prescribers/{id}")
    public ResponseEntity<ApiResponse<PrescriberDto>> getPrescriber(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPrescriberById(id)));
    }
}
