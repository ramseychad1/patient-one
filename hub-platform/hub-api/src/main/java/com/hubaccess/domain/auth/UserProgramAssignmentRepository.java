package com.hubaccess.domain.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProgramAssignmentRepository extends JpaRepository<UserProgramAssignment, UUID> {

    List<UserProgramAssignment> findByUserId(UUID userId);

    @Query("SELECT upa.program.id FROM UserProgramAssignment upa WHERE upa.user.id = :userId AND (upa.expiresAt IS NULL OR upa.expiresAt > CURRENT_TIMESTAMP)")
    List<UUID> findActiveProgamIdsByUserId(UUID userId);

    void deleteByUserIdAndProgramId(UUID userId, UUID programId);
}
