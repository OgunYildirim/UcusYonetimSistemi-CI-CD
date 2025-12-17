package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.id = :departureId " +
            "AND f.arrivalAirport.id = :arrivalId " +
            "AND DATE(f.departureTime) = DATE(:departureDate) " +
            "AND f.status != 'CANCELLED'")
    List<Flight> searchFlights(
            @Param("departureId") Long departureId,
            @Param("arrivalId") Long arrivalId,
            @Param("departureDate") LocalDateTime departureDate);

    List<Flight> findByStatus(String status);

    @Query("SELECT f FROM Flight f WHERE f.departureTime >= :startDate AND f.departureTime <= :endDate")
    List<Flight> findFlightsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
