package com.ucusyonetim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 20)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER

    @Column(nullable = false, length = 20)
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(length = 100)
    private String transactionId;

    @Column(length = 500)
    private String description;
}
