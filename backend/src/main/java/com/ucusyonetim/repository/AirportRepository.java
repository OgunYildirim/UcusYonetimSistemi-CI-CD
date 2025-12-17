package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code);

    List<Airport> findByCity(String city);

    List<Airport> findByCountry(String country);

    List<Airport> findByActiveTrue();
}
