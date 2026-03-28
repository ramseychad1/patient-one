package com.hubaccess.domain.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentRecord, UUID> {
    Optional<EnrollmentRecord> findByHubCaseId(UUID caseId);
}
