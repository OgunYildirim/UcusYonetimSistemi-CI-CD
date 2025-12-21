package com.ucusyonetim.controller;

import com.ucusyonetim.entity.Aircraft;
import com.ucusyonetim.repository.AircraftRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/aircrafts")
@RequiredArgsConstructor
@Tag(name = "Admin - Aircrafts", description = "Aircraft management APIs for Admin")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminAircraftController {

    private final AircraftRepository aircraftRepository;

    @GetMapping
    @Operation(summary = "Get all aircrafts")
    public ResponseEntity<List<Aircraft>> getAllAircrafts() {
        return ResponseEntity.ok(aircraftRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Create new aircraft")
    public ResponseEntity<Aircraft> createAircraft(@Valid @RequestBody Aircraft aircraft) {
        return ResponseEntity.ok(aircraftRepository.save(aircraft));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update aircraft")
    public ResponseEntity<Aircraft> updateAircraft(@PathVariable Long id, @Valid @RequestBody Aircraft aircraft) {
        Aircraft existingAircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));

        existingAircraft.setRegistrationNumber(aircraft.getRegistrationNumber());
        existingAircraft.setModel(aircraft.getModel());
        existingAircraft.setManufacturer(aircraft.getManufacturer());
        existingAircraft.setTotalSeats(aircraft.getTotalSeats());
        existingAircraft.setEconomySeats(aircraft.getEconomySeats());
        existingAircraft.setBusinessSeats(aircraft.getBusinessSeats());
        existingAircraft.setYearOfManufacture(aircraft.getYearOfManufacture());
        existingAircraft.setActive(aircraft.getActive());
        existingAircraft.setUnderMaintenance(aircraft.getUnderMaintenance());

        return ResponseEntity.ok(aircraftRepository.save(existingAircraft));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete aircraft")
    public ResponseEntity<String> deleteAircraft(@PathVariable Long id) {
        aircraftRepository.deleteById(id);
        return ResponseEntity.ok("Aircraft deleted successfully");
    }
}
