package com.hubaccess.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubUserRepository extends JpaRepository<HubUser, UUID> {
    Optional<HubUser> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT MAX(u.id) FROM HubUser u")
    Optional<UUID> findMaxId();
}
