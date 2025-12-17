package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByAircraftId(Long aircraftId);

    List<Seat> findByAircraftIdAndIsAvailableTrue(Long aircraftId);

    List<Seat> findByAircraftIdAndSeatClass(Long aircraftId, String seatClass);
}
