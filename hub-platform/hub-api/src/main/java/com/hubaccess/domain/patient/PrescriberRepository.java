package com.hubaccess.domain.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriberRepository extends JpaRepository<Prescriber, UUID> {
    Optional<Prescriber> findByNpi(String npi);
}
