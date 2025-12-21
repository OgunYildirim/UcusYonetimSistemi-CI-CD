package com.ucusyonetim.controller;

import com.ucusyonetim.dto.BookingRequest;
import com.ucusyonetim.dto.BookingResponse;
import com.ucusyonetim.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create new booking")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get user's bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings() {
        return ResponseEntity.ok(bookingService.getUserBookings());
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @GetMapping("/flight/{flightId}")
    @Operation(summary = "Get bookings by flight (Admin only)")
    public ResponseEntity<List<BookingResponse>> getBookingsByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(bookingService.getBookingsByFlight(flightId));
    }

    @PostMapping("/auto-assign-seat/{ticketId}")
    @Operation(summary = "Auto-assign seat to ticket (Admin only)")
    public ResponseEntity<String> autoAssignSeat(@PathVariable Long ticketId) {
        return ResponseEntity.ok(bookingService.autoAssignSeat(ticketId));
    }
}
