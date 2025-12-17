package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "aircraft_maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AircraftMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false, length = 50)
    private String maintenanceType; // ROUTINE, EMERGENCY, SCHEDULED

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    private String status; // IN_PROGRESS, COMPLETED, CANCELLED

    @Column(length = 100)
    private String performedBy;

    @Column
    private Double cost;
}
