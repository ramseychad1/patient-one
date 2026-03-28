package com.hubaccess.domain.auth;

import com.hubaccess.domain.auth.dto.AuthResponse;
import com.hubaccess.domain.auth.dto.LoginRequest;
import com.hubaccess.domain.auth.dto.RefreshRequest;
import com.hubaccess.web.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final HubUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO: Remove after initial setup — bootstrap endpoint to fix admin password
    @PostMapping("/bootstrap")
    public ResponseEntity<ApiResponse<String>> bootstrap() {
        var user = userRepository.findByEmail("admin@hub.com");
        if (user.isPresent()) {
            var u = user.get();
            u.setPasswordHash(passwordEncoder.encode("admin123"));
            userRepository.save(u);
            return ResponseEntity.ok(ApiResponse.success("Password reset for admin@hub.com"));
        }
        return ResponseEntity.ok(ApiResponse.error("NOT_FOUND", "admin@hub.com not found"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT is stateless — client discards token. Server-side refresh token invalidation is a post-MVP feature.
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
