package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private Double economyPrice;

    @Column(nullable = false)
    private Double businessPrice;

    @Column(nullable = false)
    private Double baggagePricePerKg; // 15kg ücretsiz, fazlası için kg başına ücret

    @Column(nullable = false)
    private Integer freeBaggageKg = 15;

    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    @Column
    private LocalDateTime effectiveTo;

    @Column(nullable = false)
    private Boolean active = true;
}
