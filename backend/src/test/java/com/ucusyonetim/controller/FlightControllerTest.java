package com.ucusyonetim.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucusyonetim.dto.FlightRequest;
import com.ucusyonetim.dto.FlightResponse;
import com.ucusyonetim.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * FlightController için JUnit 5 ve Mockito kullanılarak yazılmış birim testleri.
 * Veritabanı işlemleri mock'lanmıştır. Security kaldırıldı.
 */
@WebMvcTest(FlightController.class)
@DisplayName("Flight Controller Unit Tests")
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private FlightResponse flightResponse1;
    private FlightResponse flightResponse2;
    private FlightRequest flightRequest;

    @BeforeEach
    void setUp() {
        // Test verileri hazırlama
        flightResponse1 = new FlightResponse();
        flightResponse1.setId(1L);
        flightResponse1.setFlightNumber("TK001");
        flightResponse1.setDepartureAirportCode("IST");
        flightResponse1.setDepartureAirportName("Istanbul Airport");
        flightResponse1.setArrivalAirportCode("ADB");
        flightResponse1.setArrivalAirportName("Adnan Menderes Airport");
        flightResponse1.setAircraftModel("Boeing 737");
        flightResponse1.setDepartureTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        flightResponse1.setArrivalTime(LocalDateTime.of(2024, 12, 25, 11, 30));
        flightResponse1.setStatus("SCHEDULED");
        flightResponse1.setAvailableSeats(150);
        flightResponse1.setAvailableEconomySeats(120);
        flightResponse1.setAvailableBusinessSeats(30);
        flightResponse1.setEconomyPrice(BigDecimal.valueOf(500.00));
        flightResponse1.setBusinessPrice(BigDecimal.valueOf(1500.00));

        flightResponse2 = new FlightResponse();
        flightResponse2.setId(2L);
        flightResponse2.setFlightNumber("TK002");
        flightResponse2.setDepartureAirportCode("IST");
        flightResponse2.setDepartureAirportName("Istanbul Airport");
        flightResponse2.setArrivalAirportCode("ESB");
        flightResponse2.setArrivalAirportName("Esenboga Airport");
        flightResponse2.setAircraftModel("Airbus A320");
        flightResponse2.setDepartureTime(LocalDateTime.of(2024, 12, 25, 14, 0));
        flightResponse2.setArrivalTime(LocalDateTime.of(2024, 12, 25, 15, 15));
        flightResponse2.setStatus("SCHEDULED");
        flightResponse2.setAvailableSeats(180);
        flightResponse2.setAvailableEconomySeats(150);
        flightResponse2.setAvailableBusinessSeats(30);
        flightResponse2.setEconomyPrice(BigDecimal.valueOf(400.00));
        flightResponse2.setBusinessPrice(BigDecimal.valueOf(1200.00));

        flightRequest = new FlightRequest();
        flightRequest.setFlightNumber("TK003");
        flightRequest.setDepartureAirportId(1L);
        flightRequest.setArrivalAirportId(2L);
        flightRequest.setAircraftId(1L);
        flightRequest.setDepartureTime(LocalDateTime.of(2024, 12, 26, 9, 0));
        flightRequest.setArrivalTime(LocalDateTime.of(2024, 12, 26, 10, 30));
        flightRequest.setStatus("SCHEDULED");
    }

    @Test
    @DisplayName("Tüm uçuşları başarıyla getirmeli")
    void shouldGetAllFlightsSuccessfully() throws Exception {
        // Given
        List<FlightResponse> flights = Arrays.asList(flightResponse1, flightResponse2);
        when(flightService.getAllFlights()).thenReturn(flights);

        // When & Then
        mockMvc.perform(get("/api/flights/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].flightNumber", is("TK001")))
                .andExpect(jsonPath("$[0].departureAirportCode", is("IST")))
                .andExpect(jsonPath("$[0].arrivalAirportCode", is("ADB")))
                .andExpect(jsonPath("$[1].flightNumber", is("TK002")))
                .andExpect(jsonPath("$[1].departureAirportCode", is("IST")))
                .andExpect(jsonPath("$[1].arrivalAirportCode", is("ESB")));

        verify(flightService, times(1)).getAllFlights();
    }

    @Test
    @DisplayName("ID ile uçuş başarıyla getirilmeli")
    void shouldGetFlightByIdSuccessfully() throws Exception {
        // Given
        Long flightId = 1L;
        when(flightService.getFlightById(flightId)).thenReturn(flightResponse1);

        // When & Then
        mockMvc.perform(get("/api/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.flightNumber", is("TK001")))
                .andExpect(jsonPath("$.departureAirportCode", is("IST")))
                .andExpect(jsonPath("$.arrivalAirportCode", is("ADB")))
                .andExpect(jsonPath("$.aircraftModel", is("Boeing 737")))
                .andExpect(jsonPath("$.status", is("SCHEDULED")))
                .andExpect(jsonPath("$.availableSeats", is(150)));

        verify(flightService, times(1)).getFlightById(flightId);
    }

    @Test
    @DisplayName("Uçuş arama başarıyla çalışmalı")
    void shouldSearchFlightsSuccessfully() throws Exception {
        // Given
        Long departureAirportId = 1L;
        Long arrivalAirportId = 2L;
        LocalDateTime departureDate = LocalDateTime.of(2024, 12, 25, 10, 0);
        List<FlightResponse> flights = Arrays.asList(flightResponse1);

        when(flightService.searchFlights(eq(departureAirportId), eq(arrivalAirportId), any(LocalDateTime.class)))
                .thenReturn(flights);

        // When & Then
        mockMvc.perform(get("/api/flights/search")
                        .param("departureAirportId", departureAirportId.toString())
                        .param("arrivalAirportId", arrivalAirportId.toString())
                        .param("departureDate", "2024-12-25T10:00:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].flightNumber", is("TK001")))
                .andExpect(jsonPath("$[0].departureAirportCode", is("IST")))
                .andExpect(jsonPath("$[0].arrivalAirportCode", is("ADB")));

        verify(flightService, times(1)).searchFlights(eq(departureAirportId), eq(arrivalAirportId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Yeni uçuş oluşturabilmeli")
    void shouldCreateFlightSuccessfully() throws Exception {
        // Given
        FlightResponse createdFlight = new FlightResponse();
        createdFlight.setId(3L);
        createdFlight.setFlightNumber("TK003");
        createdFlight.setDepartureAirportCode("IST");
        createdFlight.setArrivalAirportCode("ADB");

        when(flightService.createFlight(any(FlightRequest.class))).thenReturn(createdFlight);

        // When & Then
        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.flightNumber", is("TK003")));

        verify(flightService, times(1)).createFlight(any(FlightRequest.class));
    }

    @Test
    @DisplayName("Uçuş güncelleyebilmeli")
    void shouldUpdateFlightSuccessfully() throws Exception {
        // Given
        Long flightId = 1L;
        FlightResponse updatedFlight = new FlightResponse();
        updatedFlight.setId(flightId);
        updatedFlight.setFlightNumber("TK001-UPDATED");
        updatedFlight.setStatus("DELAYED");

        when(flightService.updateFlight(eq(flightId), any(FlightRequest.class))).thenReturn(updatedFlight);

        // When & Then
        mockMvc.perform(put("/api/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flightRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.flightNumber", is("TK001-UPDATED")))
                .andExpect(jsonPath("$.status", is("DELAYED")));

        verify(flightService, times(1)).updateFlight(eq(flightId), any(FlightRequest.class));
    }

    @Test
    @DisplayName("Uçuş silebilmeli")
    void shouldDeleteFlightSuccessfully() throws Exception {
        // Given
        Long flightId = 1L;
        doNothing().when(flightService).deleteFlight(flightId);

        // When & Then
        mockMvc.perform(delete("/api/flights/{id}", flightId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Flight deleted successfully"));

        verify(flightService, times(1)).deleteFlight(flightId);
    }


    @Test
    @DisplayName("Boş liste döndüğünde başarılı yanıt vermeli")
    void shouldReturnEmptyListWhenNoFlightsExist() throws Exception {
        // Given
        when(flightService.getAllFlights()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/flights/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(flightService, times(1)).getAllFlights();
    }
}