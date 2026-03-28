package com.hubaccess.domain.program;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    List<Program> findByManufacturerId(UUID manufacturerId);
    List<Program> findByStatus(String status);
    List<Program> findByIdIn(List<UUID> ids);
}
