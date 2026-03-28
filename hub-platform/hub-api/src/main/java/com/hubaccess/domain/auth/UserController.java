package com.hubaccess.domain.auth;

import com.hubaccess.domain.auth.dto.*;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<Page<HubUserDto>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<HubUserDto>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(userService.createUser(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<HubUserDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<HubUserDto>> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/programs")
    @PreAuthorize("hasRole('HubAdmin')")
    public ResponseEntity<ApiResponse<Void>> assignPrograms(@PathVariable UUID id, @Valid @RequestBody AssignProgramsRequest request) {
        userService.assignPrograms(id, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
