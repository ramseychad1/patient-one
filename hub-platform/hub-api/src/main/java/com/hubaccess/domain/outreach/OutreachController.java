package com.hubaccess.domain.outreach;

import com.hubaccess.domain.outreach.dto.CreateOutreachRequest;
import com.hubaccess.domain.outreach.dto.OutreachDto;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases/{caseId}/outreach")
@RequiredArgsConstructor
public class OutreachController {

    private final OutreachService outreachService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OutreachDto>>> getByCaseId(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(outreachService.getByCaseId(caseId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OutreachDto>> send(
            @PathVariable UUID caseId, @Valid @RequestBody CreateOutreachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(outreachService.send(caseId, request)));
    }
}
