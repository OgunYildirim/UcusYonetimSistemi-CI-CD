package com.ucusyonetim.controller;

import com.ucusyonetim.dto.FlightRequest;
import com.ucusyonetim.dto.FlightResponse;
import com.ucusyonetim.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flights", description = "Flight management APIs")
@CrossOrigin(origins = "http://localhost:3000")
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/all")
    @Operation(summary = "Get all flights")
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/search")
    @Operation(summary = "Search flights")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam Long departureAirportId,
            @RequestParam Long arrivalAirportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate) {
        return ResponseEntity.ok(flightService.searchFlights(departureAirportId, arrivalAirportId, departureDate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID")
    public ResponseEntity<FlightResponse> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create new flight (Admin only)")
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody FlightRequest request) {
        return ResponseEntity.ok(flightService.createFlight(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update flight (Admin only)")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequest request) {
        return ResponseEntity.ok(flightService.updateFlight(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete flight (Admin only)")
    public ResponseEntity<String> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok("Flight deleted successfully");
    }
}
