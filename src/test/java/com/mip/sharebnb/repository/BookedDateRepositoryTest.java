package com.mip.sharebnb.repository;

import com.mip.sharebnb.exception.DataNotFoundException;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.sun.javaws.JnlpxArgs.verify;
import static org.mockito.Mockito.times;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class BookedDateRepositoryTest {

    @Autowired
    private BookedDateRepository bookedDateRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void deleteByAccommodationIdAndDateIn() {
        Reservation reservation = reservationRepository.findById(1L).orElseThrow(() -> new DataNotFoundException("예약 정보를 찾을 수 없습니다."));

        List<BookedDate> bookedDates =  reservation.getBookedDates();
        List<LocalDate> dates = new ArrayList<>();

        for (BookedDate bookedDate : bookedDates) {
            dates.add(bookedDate.getDate());
        }

        bookedDateRepository.deleteBookedDateByAccommodationIdAndDateIn(1L, dates);
    }
}