package com.hubaccess.domain.activity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseTaskRepository extends JpaRepository<CaseTask, UUID> {
    List<CaseTask> findByHubCaseId(UUID caseId);

    @Query("SELECT t FROM CaseTask t WHERE t.assignedTo.id = :userId AND t.status IN ('Open','InProgress') ORDER BY t.dueDate ASC NULLS LAST")
    Page<CaseTask> findOpenTasksByAssignedTo(UUID userId, Pageable pageable);

    @Query("SELECT t FROM CaseTask t WHERE t.assignedTo.id = :userId AND t.status IN ('Open','InProgress') AND t.hubCase.program.id IN :programIds ORDER BY t.dueDate ASC NULLS LAST")
    Page<CaseTask> findOpenTasksByAssignedToAndProgramIds(UUID userId, List<UUID> programIds, Pageable pageable);
}
