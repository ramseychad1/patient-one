package com.hubaccess.security;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.auth.HubUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubUserDetailsService implements UserDetailsService {

    private final HubUserRepository hubUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userIdOrEmail) throws UsernameNotFoundException {
        HubUser user;
        try {
            UUID userId = UUID.fromString(userIdOrEmail);
            user = hubUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userIdOrEmail));
        } catch (IllegalArgumentException e) {
            user = hubUserRepository.findByEmail(userIdOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userIdOrEmail));
        }

        var authorities = user.getUserRoles().stream()
            .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getName()))
            .toList();

        return new User(
            user.getId().toString(),
            user.getPasswordHash(),
            user.getStatus().equals("Active"),
            true, true,
            !user.getStatus().equals("Locked"),
            authorities
        );
    }
}
