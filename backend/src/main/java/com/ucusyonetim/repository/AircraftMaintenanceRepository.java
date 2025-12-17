package com.ucusyonetim.repository;

import com.ucusyonetim.entity.AircraftMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AircraftMaintenanceRepository extends JpaRepository<AircraftMaintenance, Long> {
    List<AircraftMaintenance> findByAircraftId(Long aircraftId);

    List<AircraftMaintenance> findByStatus(String status);
}
