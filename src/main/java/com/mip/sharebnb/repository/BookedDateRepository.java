package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.BookedDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookedDateRepository extends JpaRepository<BookedDate, Long> {

    List<BookedDate> findBookedDatesByAccommodationId(Long accommodationId);

    void deleteBookedDateByReservationId(Long reservationId);
}
