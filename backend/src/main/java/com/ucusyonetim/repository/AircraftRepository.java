package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    Optional<Aircraft> findByRegistrationNumber(String registrationNumber);

    List<Aircraft> findByActiveTrue();

    List<Aircraft> findByUnderMaintenanceTrue();

    List<Aircraft> findByModel(String model);
}
