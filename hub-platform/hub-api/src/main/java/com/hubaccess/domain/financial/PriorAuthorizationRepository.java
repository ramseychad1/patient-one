package com.hubaccess.domain.financial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PriorAuthorizationRepository extends JpaRepository<PriorAuthorization, UUID> {
    List<PriorAuthorization> findByHubCaseId(UUID caseId);

    @Query("SELECT pa FROM PriorAuthorization pa WHERE pa.slaSubmitDeadline <= :deadline AND pa.status = 'Draft' AND pa.slaBreached = false")
    List<PriorAuthorization> findPaSlaBreaches(LocalDate deadline);
}
