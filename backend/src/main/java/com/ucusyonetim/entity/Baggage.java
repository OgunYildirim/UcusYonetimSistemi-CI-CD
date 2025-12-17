package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "baggage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Baggage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private Double baggageFee; // 15kg ücretsiz, fazlası ücretli

    @Column(length = 20)
    private String baggageTag; // Bagaj etiketi numarası

    @Column(length = 20)
    private String baggageType; // CHECKED, CARRY_ON

    @Column(nullable = false, length = 20)
    private String status; // CHECKED_IN, LOADED, DELIVERED, LOST
}
