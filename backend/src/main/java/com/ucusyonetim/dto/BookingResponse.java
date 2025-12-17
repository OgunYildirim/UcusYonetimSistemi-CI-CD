package com.ucusyonetim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReference;
    private FlightInfo flight;
    private LocalDateTime bookingDate;
    private String status;
    private Integer numberOfPassengers;
    private Double totalPrice;
    private List<TicketInfo> tickets;
    private PaymentInfo payment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightInfo {
        private Long id;
        private String flightNumber;
        private AirportInfo departureAirport;
        private AirportInfo arrivalAirport;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private AircraftInfo aircraft;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportInfo {
        private Long id;
        private String code;
        private String name;
        private String city;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AircraftInfo {
        private Long id;
        private String model;
        private String registrationNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketInfo {
        private Long id;
        private String ticketNumber;
        private String passengerFirstName;
        private String passengerLastName;
        private String passengerPassportNumber;
        private String seatNumber;
        private String seatClass;
        private Boolean seatAssigned;
        private Boolean seatSelectionPaid;
        private Double ticketPrice;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private Long id;
        private Double amount;
        private String paymentMethod;
        private String status;
        private LocalDateTime paymentDate;
    }
}
