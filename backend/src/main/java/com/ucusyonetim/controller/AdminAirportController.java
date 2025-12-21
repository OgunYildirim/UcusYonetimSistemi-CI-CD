package com.ucusyonetim.controller;

import com.ucusyonetim.entity.Airport;
import com.ucusyonetim.repository.AirportRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/airports")
@RequiredArgsConstructor
@Tag(name = "Admin - Airports", description = "Airport management APIs for Admin")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminAirportController {

    private final AirportRepository airportRepository;

    @GetMapping
    @Operation(summary = "Get all airports")
    public ResponseEntity<List<Airport>> getAllAirports() {
        return ResponseEntity.ok(airportRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Create new airport")
    public ResponseEntity<Airport> createAirport(@Valid @RequestBody Airport airport) {
        return ResponseEntity.ok(airportRepository.save(airport));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update airport")
    public ResponseEntity<Airport> updateAirport(@PathVariable Long id, @Valid @RequestBody Airport airport) {
        Airport existingAirport = airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airport not found"));

        existingAirport.setCode(airport.getCode());
        existingAirport.setName(airport.getName());
        existingAirport.setCity(airport.getCity());
        existingAirport.setCountry(airport.getCountry());
        existingAirport.setAddress(airport.getAddress());
        existingAirport.setActive(airport.getActive());

        return ResponseEntity.ok(airportRepository.save(existingAirport));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete airport")
    public ResponseEntity<String> deleteAirport(@PathVariable Long id) {
        airportRepository.deleteById(id);
        return ResponseEntity.ok("Airport deleted successfully");
    }
}
