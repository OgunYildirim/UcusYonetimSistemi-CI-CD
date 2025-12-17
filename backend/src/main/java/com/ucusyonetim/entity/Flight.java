package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String flightNumber; // TK123, PC456, etc.

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false, length = 20)
    private String status; // SCHEDULED, BOARDING, DEPARTED, ARRIVED, CANCELLED, DELAYED

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Integer availableEconomySeats;

    @Column(nullable = false)
    private Integer availableBusinessSeats;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private Set<FlightPricing> pricings = new HashSet<>();

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();
}
