package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String ticketNumber;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false, length = 50)
    private String passengerFirstName;

    @Column(nullable = false, length = 50)
    private String passengerLastName;

    @Column(nullable = false, length = 20)
    private String passportNumber;

    @Column(nullable = false, length = 10)
    private String seatNumber;

    @Column(nullable = false, length = 20)
    private String seatClass; // ECONOMY, BUSINESS

    @Column(nullable = false)
    private Boolean seatAssigned = false; // Koltuk atandı mı?

    @Column(nullable = false)
    private Boolean seatSelectionPaid = false; // Koltuk seçimi için ödeme yapıldı mı?

    @Column(nullable = false)
    private Double ticketPrice;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, CANCELLED, USED

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private Set<Baggage> baggages = new HashSet<>();
}
