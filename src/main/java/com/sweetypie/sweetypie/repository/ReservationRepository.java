package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation as r where r.member.id = :member_id")
    List<Reservation> findReservationByMemberId(@Param("member_id") Long id);

}
