package com.ucusyonetim.service;

import com.ucusyonetim.entity.Aircraft;
import com.ucusyonetim.entity.Seat;
import com.ucusyonetim.repository.AircraftRepository;
import com.ucusyonetim.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final AircraftRepository aircraftRepository;

    public List<Seat> getSeatsByAircraft(Long aircraftId) {
        return seatRepository.findByAircraftId(aircraftId);
    }

    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public Seat updateSeat(Long id, Seat seatDetails) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));

        seat.setSeatNumber(seatDetails.getSeatNumber());
        seat.setSeatClass(seatDetails.getSeatClass());
        seat.setIsAvailable(seatDetails.getIsAvailable());
        seat.setIsWindowSeat(seatDetails.getIsWindowSeat());
        seat.setIsAisleSeat(seatDetails.getIsAisleSeat());

        return seatRepository.save(seat);
    }

    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    @Transactional
    public List<Seat> generateSeatsForAircraft(Long aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new RuntimeException("Aircraft not found with id: " + aircraftId));

        // Check if seats already exist
        List<Seat> existingSeats = seatRepository.findByAircraftId(aircraftId);
        if (!existingSeats.isEmpty()) {
            throw new RuntimeException("Seats already exist for this aircraft");
        }

        List<Seat> seats = new ArrayList<>();

        // Generate Business Class seats (2-2 configuration)
        int businessRows = (int) Math.ceil(aircraft.getBusinessSeats() / 4.0);
        char[] businessColumns = { 'A', 'B', 'C', 'D' };

        for (int row = 1; row <= businessRows; row++) {
            for (int col = 0; col < 4 && seats.size() < aircraft.getBusinessSeats(); col++) {
                Seat seat = new Seat();
                seat.setAircraft(aircraft);
                seat.setSeatNumber(row + String.valueOf(businessColumns[col]));
                seat.setSeatClass("BUSINESS");
                seat.setIsAvailable(true);
                seat.setIsWindowSeat(col == 0 || col == 3);
                seat.setIsAisleSeat(col == 1 || col == 2);
                seats.add(seat);
            }
        }

        // Generate Economy Class seats (3-3 configuration)
        int economyStartRow = businessRows + 1;
        int economyRows = (int) Math.ceil(aircraft.getEconomySeats() / 6.0);
        char[] economyColumns = { 'A', 'B', 'C', 'D', 'E', 'F' };

        for (int row = economyStartRow; row < economyStartRow + economyRows; row++) {
            for (int col = 0; col < 6
                    && (seats.size() - aircraft.getBusinessSeats()) < aircraft.getEconomySeats(); col++) {
                Seat seat = new Seat();
                seat.setAircraft(aircraft);
                seat.setSeatNumber(row + String.valueOf(economyColumns[col]));
                seat.setSeatClass("ECONOMY");
                seat.setIsAvailable(true);
                seat.setIsWindowSeat(col == 0 || col == 5);
                seat.setIsAisleSeat(col == 2 || col == 3);
                seats.add(seat);
            }
        }

        return seatRepository.saveAll(seats);
    }

    public Seat autoAssignSeat(Long aircraftId, String seatClass) {
        // Find available seat for the given class
        List<Seat> availableSeats = seatRepository.findByAircraftId(aircraftId).stream()
                .filter(s -> s.getIsAvailable() && s.getSeatClass().equals(seatClass))
                .toList();

        if (availableSeats.isEmpty()) {
            throw new RuntimeException("No available seats for class: " + seatClass);
        }

        // Return first available seat
        return availableSeats.get(0);
    }
}
