package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.BookedDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookedDateRepository extends JpaRepository<BookedDate, Long> {

    List<BookedDate> findBookedDatesByAccommodationId(Long accommodationId);

    void deleteBookedDateByReservationId(Long reservationId);
}
