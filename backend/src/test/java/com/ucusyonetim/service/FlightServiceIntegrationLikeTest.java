package com.ucusyonetim.service;

import com.ucusyonetim.dto.FlightRequest;
import com.ucusyonetim.dto.FlightResponse;
import com.ucusyonetim.entity.Aircraft;
import com.ucusyonetim.entity.Airport;
import com.ucusyonetim.entity.Flight;
import com.ucusyonetim.repository.AircraftRepository;
import com.ucusyonetim.repository.AirportRepository;
import com.ucusyonetim.repository.FlightPricingRepository;
import com.ucusyonetim.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightServiceIntegrationLikeTest {
    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private FlightPricingRepository flightPricingRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight sampleFlight;
    private Airport sampleDeparture;
    private Airport sampleArrival;
    private Aircraft sampleAircraft;

    @BeforeEach
    void setUp() {
        // Airport ve Aircraft nesnelerini de oluşturuyoruz, çünkü convertToResponse bunlara erişiyor
        sampleDeparture = new Airport();
        sampleDeparture.setId(10L);
        sampleDeparture.setCode("IST");
        sampleDeparture.setName("Istanbul Airport");

        sampleArrival = new Airport();
        sampleArrival.setId(20L);
        sampleArrival.setCode("ESB");
        sampleArrival.setName("Esenboğa Airport");

        sampleAircraft = new Aircraft();
        sampleAircraft.setId(5L);
        sampleAircraft.setModel("Airbus A320");
        sampleAircraft.setUnderMaintenance(false);
        sampleAircraft.setTotalSeats(180);
        sampleAircraft.setEconomySeats(150);
        sampleAircraft.setBusinessSeats(30);

        // Basit bir Flight nesnesi oluşturuyoruz (veritabanı yerine mock kullanılıyor)
        sampleFlight = new Flight();
        sampleFlight.setId(1L);
        sampleFlight.setFlightNumber("TK123");
        sampleFlight.setDepartureTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        sampleFlight.setArrivalTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        // service.convertToResponse erişeceği için departureAirport/arrivalAirport/aircraft set ediliyor
        sampleFlight.setDepartureAirport(sampleDeparture);
        sampleFlight.setArrivalAirport(sampleArrival);
        sampleFlight.setAircraft(sampleAircraft);
        sampleFlight.setStatus("SCHEDULED");
        sampleFlight.setAvailableSeats(180);
        sampleFlight.setAvailableEconomySeats(150);
        sampleFlight.setAvailableBusinessSeats(30);
    }

    @Test
    void whenGetFlightById_thenReturnFlightResponse() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(sampleFlight));
        // flightPricingRepository'yi boş dönecek şekilde bırakıyoruz
        when(flightPricingRepository.findActiveByFlightId(1L)).thenReturn(Optional.empty());

        // Act
        FlightResponse resp = flightService.getFlightById(1L);

        // Assert
        assertNotNull(resp, "Response should not be null");
        assertEquals(sampleFlight.getId(), resp.getId());
        assertEquals(sampleFlight.getFlightNumber(), resp.getFlightNumber());
        assertEquals(sampleDeparture.getCode(), resp.getDepartureAirportCode());
        assertEquals(sampleArrival.getCode(), resp.getArrivalAirportCode());
        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    void whenCreateFlight_thenRepositorySaveCalled() {
        // Arrange
        FlightRequest req = new FlightRequest();
        req.setFlightNumber("TK999");
        req.setDepartureAirportId(sampleDeparture.getId());
        req.setArrivalAirportId(sampleArrival.getId());
        req.setAircraftId(sampleAircraft.getId());
        req.setDepartureTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        req.setArrivalTime(LocalDateTime.of(2025, 2, 1, 12, 0));
        req.setStatus("SCHEDULED");

        when(airportRepository.findById(sampleDeparture.getId())).thenReturn(Optional.of(sampleDeparture));
        when(airportRepository.findById(sampleArrival.getId())).thenReturn(Optional.of(sampleArrival));
        when(aircraftRepository.findById(sampleAircraft.getId())).thenReturn(Optional.of(sampleAircraft));

        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> {
            Flight f = invocation.getArgument(0);
            f.setId(99L);
            return f;
        });

        when(flightPricingRepository.findActiveByFlightId(anyLong())).thenReturn(Optional.empty());

        // Act
        FlightResponse created = flightService.createFlight(req);

        // Assert
        assertNotNull(created);
        assertEquals(99L, created.getId());
        assertEquals("TK999", created.getFlightNumber());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }
}
