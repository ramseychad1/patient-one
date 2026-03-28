package com.hubaccess.domain.cases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseStatusHistoryRepository extends JpaRepository<CaseStatusHistory, UUID> {
    List<CaseStatusHistory> findByHubCaseIdOrderByChangedAtDesc(UUID caseId);
}
