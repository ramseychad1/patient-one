package com.hubaccess.domain.outreach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutreachRepository extends JpaRepository<PatientOutreach, UUID> {
    List<PatientOutreach> findByHubCaseId(UUID caseId);

    @Query("SELECT o FROM PatientOutreach o WHERE o.responded = false AND o.outreachType IN ('Consent','MI') ORDER BY o.sentAt ASC")
    List<PatientOutreach> findUnrespondedConsentAndMi();
}
