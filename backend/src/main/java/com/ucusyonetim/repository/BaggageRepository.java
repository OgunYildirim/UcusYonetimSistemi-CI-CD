package com.ucusyonetim.repository;

import com.ucusyonetim.entity.Baggage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BaggageRepository extends JpaRepository<Baggage, Long> {
    List<Baggage> findByTicketId(Long ticketId);

    List<Baggage> findByStatus(String status);
}
