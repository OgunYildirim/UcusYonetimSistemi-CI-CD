package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(nullable = false, length = 10)
    private String seatNumber; // 1A, 1B, 2A, 2B, etc.

    @Column(nullable = false, length = 20)
    private String seatClass; // ECONOMY, BUSINESS

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private Boolean isWindowSeat = false;

    @Column(nullable = false)
    private Boolean isAisleSeat = false;
}
