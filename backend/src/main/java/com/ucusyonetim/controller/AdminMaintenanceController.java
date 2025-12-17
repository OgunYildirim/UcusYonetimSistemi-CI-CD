package com.ucusyonetim.controller;

import com.ucusyonetim.entity.AircraftMaintenance;
import com.ucusyonetim.repository.AircraftMaintenanceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/maintenance")
@RequiredArgsConstructor
@Tag(name = "Admin - Maintenance", description = "Aircraft maintenance management APIs for Admin")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminMaintenanceController {

    private final AircraftMaintenanceRepository maintenanceRepository;

    @GetMapping
    @Operation(summary = "Get all maintenance records")
    public ResponseEntity<List<AircraftMaintenance>> getAllMaintenance() {
        return ResponseEntity.ok(maintenanceRepository.findAll());
    }

    @GetMapping("/aircraft/{aircraftId}")
    @Operation(summary = "Get maintenance records by aircraft")
    public ResponseEntity<List<AircraftMaintenance>> getMaintenanceByAircraft(@PathVariable Long aircraftId) {
        return ResponseEntity.ok(maintenanceRepository.findByAircraftId(aircraftId));
    }

    @PostMapping
    @Operation(summary = "Create new maintenance record")
    public ResponseEntity<AircraftMaintenance> createMaintenance(@Valid @RequestBody AircraftMaintenance maintenance) {
        return ResponseEntity.ok(maintenanceRepository.save(maintenance));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update maintenance record")
    public ResponseEntity<AircraftMaintenance> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody AircraftMaintenance maintenance) {
        AircraftMaintenance existing = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));

        existing.setStartDate(maintenance.getStartDate());
        existing.setEndDate(maintenance.getEndDate());
        existing.setMaintenanceType(maintenance.getMaintenanceType());
        existing.setDescription(maintenance.getDescription());
        existing.setStatus(maintenance.getStatus());
        existing.setPerformedBy(maintenance.getPerformedBy());
        existing.setCost(maintenance.getCost());

        return ResponseEntity.ok(maintenanceRepository.save(existing));
    }
}
