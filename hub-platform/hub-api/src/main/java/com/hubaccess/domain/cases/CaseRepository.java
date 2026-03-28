package com.hubaccess.domain.cases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<HubCase, UUID> {

    @Query("SELECT c FROM HubCase c WHERE c.program.id IN :programIds")
    Page<HubCase> findByProgramIdIn(List<UUID> programIds, Pageable pageable);

    Page<HubCase> findAll(Pageable pageable);

    Optional<HubCase> findByCaseNumber(String caseNumber);

    @Query("SELECT c FROM HubCase c WHERE c.assignedCm.id = :cmId AND c.program.id IN :programIds")
    Page<HubCase> findByAssignedCmIdAndProgramIdIn(UUID cmId, List<UUID> programIds, Pageable pageable);

    @Query("SELECT c FROM HubCase c WHERE c.assignedCm.id = :cmId")
    Page<HubCase> findByAssignedCmId(UUID cmId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM HubCase c WHERE c.program.id IN :programIds")
    long countByProgramIdIn(List<UUID> programIds);

    @Query("SELECT MAX(CAST(SUBSTRING(c.caseNumber, 10) AS integer)) FROM HubCase c WHERE c.caseNumber LIKE :prefix")
    Optional<Integer> findMaxCaseNumberSequence(String prefix);
}
