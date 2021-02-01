package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
