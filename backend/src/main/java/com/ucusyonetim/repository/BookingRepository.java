package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByFlightId(Long flightId);

    List<Booking> findByStatus(String status);
}
