package com.ucusyonetim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    private Long id;
    private String flightNumber;
    private String departureAirportCode;
    private String departureAirportName;
    private String arrivalAirportCode;
    private String arrivalAirportName;
    private String aircraftModel;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;
    private Integer availableSeats;
    private Integer availableEconomySeats;
    private Integer availableBusinessSeats;
    private Double economyPrice;
    private Double businessPrice;
}
