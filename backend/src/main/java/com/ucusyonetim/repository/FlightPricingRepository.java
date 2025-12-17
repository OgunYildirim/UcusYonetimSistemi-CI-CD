package com.ucusyonetim.repository;

import com.ucusyonetim.entity.FlightPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightPricingRepository extends JpaRepository<FlightPricing, Long> {
    List<FlightPricing> findByFlightId(Long flightId);

    @Query("SELECT fp FROM FlightPricing fp WHERE fp.flight.id = :flightId AND fp.active = true")
    Optional<FlightPricing> findActiveByFlightId(@Param("flightId") Long flightId);
}
