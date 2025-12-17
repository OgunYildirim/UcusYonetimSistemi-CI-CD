package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "aircrafts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber; // TC-JRO, TC-JRE, etc.

    @Column(nullable = false, length = 50)
    private String model; // Boeing 737, Airbus A320, etc.

    @Column(nullable = false, length = 50)
    private String manufacturer; // Boeing, Airbus, etc.

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer economySeats;

    @Column(nullable = false)
    private Integer businessSeats;

    @Column(nullable = false)
    private Integer yearOfManufacture;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean underMaintenance = false;

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL)
    private Set<Flight> flights = new HashSet<>();

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL)
    private Set<AircraftMaintenance> maintenanceRecords = new HashSet<>();

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL)
    private Set<Seat> seats = new HashSet<>();
}
