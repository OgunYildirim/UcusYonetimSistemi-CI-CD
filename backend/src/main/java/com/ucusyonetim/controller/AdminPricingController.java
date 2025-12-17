package com.ucusyonetim.controller;

import com.ucusyonetim.entity.FlightPricing;
import com.ucusyonetim.repository.FlightPricingRepository;
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
@RequestMapping("/api/admin/pricing")
@RequiredArgsConstructor
@Tag(name = "Admin - Pricing", description = "Flight pricing management APIs for Admin")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminPricingController {

    private final FlightPricingRepository pricingRepository;

    @GetMapping
    @Operation(summary = "Get all pricing records")
    public ResponseEntity<List<FlightPricing>> getAllPricing() {
        return ResponseEntity.ok(pricingRepository.findAll());
    }

    @GetMapping("/flight/{flightId}")
    @Operation(summary = "Get pricing by flight")
    public ResponseEntity<List<FlightPricing>> getPricingByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(pricingRepository.findByFlightId(flightId));
    }

    @PostMapping
    @Operation(summary = "Create new pricing")
    public ResponseEntity<FlightPricing> createPricing(@Valid @RequestBody FlightPricing pricing) {
        return ResponseEntity.ok(pricingRepository.save(pricing));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pricing")
    public ResponseEntity<FlightPricing> updatePricing(
            @PathVariable Long id,
            @Valid @RequestBody FlightPricing pricing) {
        FlightPricing existing = pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing not found"));

        existing.setEconomyPrice(pricing.getEconomyPrice());
        existing.setBusinessPrice(pricing.getBusinessPrice());
        existing.setBaggagePricePerKg(pricing.getBaggagePricePerKg());
        existing.setFreeBaggageKg(pricing.getFreeBaggageKg());
        existing.setEffectiveFrom(pricing.getEffectiveFrom());
        existing.setEffectiveTo(pricing.getEffectiveTo());
        existing.setActive(pricing.getActive());

        return ResponseEntity.ok(pricingRepository.save(existing));
    }
}
