package com.ucusyonetim.controller;

import com.ucusyonetim.entity.Airport;
import com.ucusyonetim.repository.AirportRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
@Tag(name = "Airports", description = "Airport APIs")
@CrossOrigin(origins = "http://localhost:3000")
public class AirportController {

    private final AirportRepository airportRepository;

    @GetMapping
    @Operation(summary = "Get all active airports")
    public ResponseEntity<List<Airport>> getAllActiveAirports() {
        return ResponseEntity.ok(airportRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get airport by ID")
    public ResponseEntity<Airport> getAirportById(@PathVariable Long id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airport not found"));
        return ResponseEntity.ok(airport);
    }
}
