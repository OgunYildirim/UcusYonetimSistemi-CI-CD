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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Flight Service Unit Tests")
class FlightServiceTest {

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

    private Airport departureAirport;
    private Airport arrivalAirport;
    private Aircraft aircraft;
    private Flight flight;
    private FlightRequest flightRequest;
    private FlightPricing flightPricing;

    @BeforeEach
    void setUp() {
        // Havalimanı test verileri
        departureAirport = new Airport();
        departureAirport.setId(1L);
        departureAirport.setCode("IST");
        departureAirport.setName("Istanbul Airport");
        departureAirport.setCity("Istanbul");
        departureAirport.setCountry("Turkey");

        arrivalAirport = new Airport();
        arrivalAirport.setId(2L);
        arrivalAirport.setCode("ADB");
        arrivalAirport.setName("Adnan Menderes Airport");
        arrivalAirport.setCity("Izmir");
        arrivalAirport.setCountry("Turkey");

        // Uçak test verileri
        aircraft = new Aircraft();
        aircraft.setId(1L);
        aircraft.setModel("Boeing 737");
        aircraft.setRegistrationNumber("TC-JKL");
        aircraft.setTotalSeats(150);
        aircraft.setEconomySeats(120);
        aircraft.setBusinessSeats(30);
        aircraft.setUnderMaintenance(false);

        // Uçuş test verileri
        flight = new Flight();
        flight.setId(1L);
        flight.setFlightNumber("TK001");
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        flight.setArrivalTime(LocalDateTime.of(2024, 12, 25, 11, 30));
        flight.setStatus("SCHEDULED");
        flight.setAvailableSeats(150);
        flight.setAvailableEconomySeats(120);
        flight.setAvailableBusinessSeats(30);

        // Fiyatlandırma test verileri
        flightPricing = new FlightPricing();
        flightPricing.setId(1L);
        flightPricing.setFlight(flight);
        flightPricing.setEconomyPrice(BigDecimal.valueOf(500.00));
        flightPricing.setBusinessPrice(BigDecimal.valueOf(1500.00));
        flightPricing.setActive(true);

        // Request test verileri
        flightRequest = new FlightRequest();
        flightRequest.setFlightNumber("TK001");
        flightRequest.setDepartureAirportId(1L);
        flightRequest.setArrivalAirportId(2L);
        flightRequest.setAircraftId(1L);
        flightRequest.setDepartureTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        flightRequest.setArrivalTime(LocalDateTime.of(2024, 12, 25, 11, 30));
        flightRequest.setStatus("SCHEDULED");
    }

    @Test
    @DisplayName("Yeni uçuş başarıyla oluşturulmalı")
    void shouldCreateFlightSuccessfully() {
        // Given
        when(airportRepository.findById(1L)).thenReturn(Optional.of(departureAirport));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(arrivalAirport));
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
        when(flightPricingRepository.findActiveByFlightId(anyLong())).thenReturn(Optional.of(flightPricing));

        // When
        FlightResponse result = flightService.createFlight(flightRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFlightNumber()).isEqualTo("TK001");
        assertThat(result.getDepartureAirportCode()).isEqualTo("IST");
        assertThat(result.getArrivalAirportCode()).isEqualTo("ADB");
        assertThat(result.getAircraftModel()).isEqualTo("Boeing 737");
        assertThat(result.getAvailableSeats()).isEqualTo(150);
        assertThat(result.getAvailableEconomySeats()).isEqualTo(120);
        assertThat(result.getAvailableBusinessSeats()).isEqualTo(30);

        verify(airportRepository, times(1)).findById(1L);
        verify(airportRepository, times(1)).findById(2L);
        verify(aircraftRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    @DisplayName("Bakımdaki uçak ile uçuş oluşturulamamalı")
    void shouldNotCreateFlightWithAircraftUnderMaintenance() {
        // Given
        aircraft.setUnderMaintenance(true);
        when(airportRepository.findById(1L)).thenReturn(Optional.of(departureAirport));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(arrivalAirport));
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));

        // When & Then
        assertThatThrownBy(() -> flightService.createFlight(flightRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Aircraft is under maintenance");

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    @DisplayName("Olmayan havalimanı ile uçuş oluşturulamamalı")
    void shouldNotCreateFlightWithNonExistentAirport() {
        // Given
        when(airportRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flightService.createFlight(flightRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Departure airport not found");

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    @DisplayName("Olmayan uçak ile uçuş oluşturulamamalı")
    void shouldNotCreateFlightWithNonExistentAircraft() {
        // Given
        when(airportRepository.findById(1L)).thenReturn(Optional.of(departureAirport));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(arrivalAirport));
        when(aircraftRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flightService.createFlight(flightRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Aircraft not found");

        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    @DisplayName("Tüm uçuşlar başarıyla getirilmeli")
    void shouldGetAllFlightsSuccessfully() {
        // Given
        Flight flight2 = new Flight();
        flight2.setId(2L);
        flight2.setFlightNumber("TK002");
        flight2.setDepartureAirport(departureAirport);
        flight2.setArrivalAirport(arrivalAirport);
        flight2.setAircraft(aircraft);
        flight2.setDepartureTime(LocalDateTime.of(2024, 12, 25, 14, 0));
        flight2.setArrivalTime(LocalDateTime.of(2024, 12, 25, 15, 30));
        flight2.setStatus("SCHEDULED");

        List<Flight> flights = Arrays.asList(flight, flight2);
        when(flightRepository.findAll()).thenReturn(flights);
        when(flightPricingRepository.findActiveByFlightId(anyLong())).thenReturn(Optional.of(flightPricing));

        // When
        List<FlightResponse> result = flightService.getAllFlights();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFlightNumber()).isEqualTo("TK001");
        assertThat(result.get(1).getFlightNumber()).isEqualTo("TK002");

        verify(flightRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID ile uçuş başarıyla getirilmeli")
    void shouldGetFlightByIdSuccessfully() {
        // Given
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPricingRepository.findActiveByFlightId(1L)).thenReturn(Optional.of(flightPricing));

        // When
        FlightResponse result = flightService.getFlightById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFlightNumber()).isEqualTo("TK001");
        assertThat(result.getDepartureAirportCode()).isEqualTo("IST");
        assertThat(result.getArrivalAirportCode()).isEqualTo("ADB");
        assertThat(result.getEconomyPrice()).isEqualTo(BigDecimal.valueOf(500.00));
        assertThat(result.getBusinessPrice()).isEqualTo(BigDecimal.valueOf(1500.00));

        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Olmayan ID ile uçuş getirilememeli")
    void shouldNotGetFlightWithNonExistentId() {
        // Given
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flightService.getFlightById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Flight not found");

        verify(flightRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Uçuş başarıyla güncellenebilmeli")
    void shouldUpdateFlightSuccessfully() {
        // Given
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(airportRepository.findById(1L)).thenReturn(Optional.of(departureAirport));
        when(airportRepository.findById(2L)).thenReturn(Optional.of(arrivalAirport));
        when(aircraftRepository.findById(1L)).thenReturn(Optional.of(aircraft));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
        when(flightPricingRepository.findActiveByFlightId(anyLong())).thenReturn(Optional.of(flightPricing));

        flightRequest.setStatus("DELAYED");

        // When
        FlightResponse result = flightService.updateFlight(1L, flightRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(flightRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    @DisplayName("Uçuş başarıyla silinebilmeli")
    void shouldDeleteFlightSuccessfully() {
        // Given
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        doNothing().when(flightRepository).delete(flight);

        // When
        flightService.deleteFlight(1L);

        // Then
        verify(flightRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).delete(flight);
    }

    @Test
    @DisplayName("Olmayan uçuş silinemez")
    void shouldNotDeleteNonExistentFlight() {
        // Given
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flightService.deleteFlight(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Flight not found");

        verify(flightRepository, times(1)).findById(999L);
        verify(flightRepository, never()).delete(any(Flight.class));
    }

    @Test
    @DisplayName("Uçuş arama başarıyla çalışmalı")
    void shouldSearchFlightsSuccessfully() {
        // Given
        LocalDateTime departureDate = LocalDateTime.of(2024, 12, 25, 10, 0);
        List<Flight> flights = Arrays.asList(flight);
        
        when(flightRepository.searchFlights(1L, 2L, departureDate)).thenReturn(flights);
        when(flightPricingRepository.findActiveByFlightId(anyLong())).thenReturn(Optional.of(flightPricing));

        // When
        List<FlightResponse> result = flightService.searchFlights(1L, 2L, departureDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFlightNumber()).isEqualTo("TK001");
        assertThat(result.get(0).getDepartureAirportCode()).isEqualTo("IST");
        assertThat(result.get(0).getArrivalAirportCode()).isEqualTo("ADB");

        verify(flightRepository, times(1)).searchFlights(1L, 2L, departureDate);
    }

    @Test
    @DisplayName("Fiyatlandırma olmadan uçuş response oluşturulabilmeli")
    void shouldCreateFlightResponseWithoutPricing() {
        // Given
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightPricingRepository.findActiveByFlightId(1L)).thenReturn(Optional.empty());

        // When
        FlightResponse result = flightService.getFlightById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFlightNumber()).isEqualTo("TK001");
        assertThat(result.getEconomyPrice()).isNull();
        assertThat(result.getBusinessPrice()).isNull();

        verify(flightRepository, times(1)).findById(1L);
        verify(flightPricingRepository, times(1)).findActiveByFlightId(1L);
    }
}
