package com.hubaccess.domain.patient;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "prescriber")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescriber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 10)
    private String npi;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(length = 50)
    private String credential;

    @Column(name = "practice_name", length = 200)
    private String practiceName;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String fax;

    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(length = 10)
    private String zip;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
