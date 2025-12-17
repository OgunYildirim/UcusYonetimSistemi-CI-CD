package com.ucusyonetim.controller;

import com.ucusyonetim.entity.Seat;
import com.ucusyonetim.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/seats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Seats", description = "Seat management APIs for admins")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminSeatController {

    private final SeatService seatService;

    @GetMapping("/aircraft/{aircraftId}")
    @Operation(summary = "Get all seats for an aircraft")
    public ResponseEntity<List<Seat>> getSeatsByAircraft(@PathVariable Long aircraftId) {
        List<Seat> seats = seatService.getSeatsByAircraft(aircraftId);
        return ResponseEntity.ok(seats);
    }

    @PostMapping
    @Operation(summary = "Create a new seat")
    public ResponseEntity<Seat> createSeat(@RequestBody Seat seat) {
        Seat created = seatService.createSeat(seat);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a seat")
    public ResponseEntity<Seat> updateSeat(@PathVariable Long id, @RequestBody Seat seat) {
        Seat updated = seatService.updateSeat(id, seat);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a seat")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/{aircraftId}")
    @Operation(summary = "Auto-generate seats for an aircraft")
    public ResponseEntity<List<Seat>> generateSeats(@PathVariable Long aircraftId) {
        List<Seat> seats = seatService.generateSeatsForAircraft(aircraftId);
        return ResponseEntity.ok(seats);
    }
}
