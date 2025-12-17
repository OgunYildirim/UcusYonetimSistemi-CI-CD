package com.ucusyonetim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Passenger details are required")
    private List<PassengerInfo> passengers;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerInfo {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Passport number is required")
        private String passportNumber;

        @NotBlank(message = "Seat number is required")
        private String seatNumber;

        @NotBlank(message = "Seat class is required")
        private String seatClass;

        @Positive(message = "Baggage weight must be positive")
        private Double baggageWeightKg;
    }
}
