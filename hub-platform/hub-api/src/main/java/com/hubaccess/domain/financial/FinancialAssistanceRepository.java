package com.hubaccess.domain.financial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialAssistanceRepository extends JpaRepository<FinancialAssistanceCase, UUID> {
    List<FinancialAssistanceCase> findByHubCaseId(UUID caseId);
    List<FinancialAssistanceCase> findByHubCaseIdAndFaType(UUID caseId, String faType);
}
