package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByBookingId(Long bookingId);

    List<Ticket> findByStatus(String status);
}
