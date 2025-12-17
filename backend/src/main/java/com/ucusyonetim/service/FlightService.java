package com.ucusyonetim.service;

import com.ucusyonetim.dto.FlightRequest;
import com.ucusyonetim.dto.FlightResponse;
import com.ucusyonetim.entity.Aircraft;
import com.ucusyonetim.entity.Airport;
import com.ucusyonetim.entity.Flight;
import com.ucusyonetim.entity.FlightPricing;
import com.ucusyonetim.repository.AircraftRepository;
import com.ucusyonetim.repository.AirportRepository;
import com.ucusyonetim.repository.FlightPricingRepository;
import com.ucusyonetim.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;
    private final FlightPricingRepository flightPricingRepository;

    @Transactional
    public FlightResponse createFlight(FlightRequest request) {
        Airport departureAirport = airportRepository.findById(request.getDepartureAirportId())
                .orElseThrow(() -> new RuntimeException("Departure airport not found"));

        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId())
                .orElseThrow(() -> new RuntimeException("Arrival airport not found"));

        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));

        if (aircraft.getUnderMaintenance()) {
            throw new RuntimeException("Aircraft is under maintenance");
        }

        Flight flight = new Flight();
        flight.setFlightNumber(request.getFlightNumber());
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setStatus(request.getStatus());
        flight.setAvailableSeats(aircraft.getTotalSeats());
        flight.setAvailableEconomySeats(aircraft.getEconomySeats());
        flight.setAvailableBusinessSeats(aircraft.getBusinessSeats());

        Flight savedFlight = flightRepository.save(flight);
        return convertToResponse(savedFlight);
    }

    public List<FlightResponse> searchFlights(Long departureAirportId, Long arrivalAirportId,
            LocalDateTime departureDate) {
        List<Flight> flights = flightRepository.searchFlights(departureAirportId, arrivalAirportId, departureDate);
        return flights.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return convertToResponse(flight);
    }

    @Transactional
    public FlightResponse updateFlight(Long id, FlightRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        Airport departureAirport = airportRepository.findById(request.getDepartureAirportId())
                .orElseThrow(() -> new RuntimeException("Departure airport not found"));

        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId())
                .orElseThrow(() -> new RuntimeException("Arrival airport not found"));

        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new RuntimeException("Aircraft not found"));

        flight.setFlightNumber(request.getFlightNumber());
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setStatus(request.getStatus());

        Flight updatedFlight = flightRepository.save(flight);
        return convertToResponse(updatedFlight);
    }

    @Transactional
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        flightRepository.delete(flight);
    }

    private FlightResponse convertToResponse(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setDepartureAirportCode(flight.getDepartureAirport().getCode());
        response.setDepartureAirportName(flight.getDepartureAirport().getName());
        response.setArrivalAirportCode(flight.getArrivalAirport().getCode());
        response.setArrivalAirportName(flight.getArrivalAirport().getName());
        response.setAircraftModel(flight.getAircraft().getModel());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setStatus(flight.getStatus());
        response.setAvailableSeats(flight.getAvailableSeats());
        response.setAvailableEconomySeats(flight.getAvailableEconomySeats());
        response.setAvailableBusinessSeats(flight.getAvailableBusinessSeats());

        // Get pricing information
        flightPricingRepository.findActiveByFlightId(flight.getId()).ifPresent(pricing -> {
            response.setEconomyPrice(pricing.getEconomyPrice());
            response.setBusinessPrice(pricing.getBusinessPrice());
        });

        return response;
    }
}
