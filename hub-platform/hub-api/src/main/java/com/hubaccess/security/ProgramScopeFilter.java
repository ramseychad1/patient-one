package com.hubaccess.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramScopeFilter extends OncePerRequestFilter {

    public static final String PROGRAM_IDS_ATTRIBUTE = "hub.programIds";
    public static final String IS_HUB_ADMIN_ATTRIBUTE = "hub.isAdmin";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HubAdmin"));
            request.setAttribute(IS_HUB_ADMIN_ATTRIBUTE, isAdmin);

            if (!isAdmin && auth.getCredentials() instanceof String token) {
                // Program IDs will be set from JWT claims in a real scenario
                request.setAttribute(PROGRAM_IDS_ATTRIBUTE, Collections.emptyList());
            }
        }

        filterChain.doFilter(request, response);
    }
}
