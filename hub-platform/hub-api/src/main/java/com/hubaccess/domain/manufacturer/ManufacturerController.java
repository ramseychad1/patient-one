package com.hubaccess.domain.manufacturer;

import com.hubaccess.domain.manufacturer.dto.CreateManufacturerRequest;
import com.hubaccess.domain.manufacturer.dto.ManufacturerDto;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ManufacturerDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(manufacturerService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<ManufacturerDto>> create(@Valid @RequestBody CreateManufacturerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(manufacturerService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ManufacturerDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(manufacturerService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<ManufacturerDto>> update(@PathVariable UUID id, @Valid @RequestBody CreateManufacturerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(manufacturerService.update(id, request)));
    }
}
