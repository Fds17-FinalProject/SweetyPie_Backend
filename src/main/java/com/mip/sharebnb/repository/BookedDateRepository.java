package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.BookedDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookedDateRepository extends JpaRepository<BookedDate, Long> {

    List<BookedDate> findBookedDatesByAccommodation_Id(Long accommodationId);

    List<BookedDate> findBookedDatesByReservationId(Long reservationId);

    void deleteBookedDateByReservationId(Long reservationId);
}
