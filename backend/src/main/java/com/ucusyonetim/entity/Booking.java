package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String bookingReference; // PNR kodu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(nullable = false, length = 20)
    private String status; // CONFIRMED, CANCELLED, PENDING

    @Column(nullable = false)
    private Integer numberOfPassengers;

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private Set<Ticket> tickets = new HashSet<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private Set<Payment> payments = new HashSet<>();
}
