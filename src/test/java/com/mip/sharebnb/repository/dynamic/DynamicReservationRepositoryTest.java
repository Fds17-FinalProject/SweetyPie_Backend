package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class DynamicReservationRepositoryTest {

    @Autowired
    private DynamicReservationRepository dynamicReservationRepository;

    @Test
    void duplicateReservationDate(){

        List<BookedDate> findBookedDate = dynamicReservationRepository.findByReservationIdAndDate(1L, LocalDate.of(2020, 2, 20), LocalDate.of(2020, 2, 22));

        assertThat(findBookedDate.size()).isEqualTo(3);
        assertThat(findBookedDate.get(0).getDate()).isEqualTo("2020-02-20");
        assertThat(findBookedDate.get(1).getDate()).isEqualTo("2020-02-21");
        assertThat(findBookedDate.get(2).getDate()).isEqualTo("2020-02-22");
    }

    @Test
    void nonDuplicateReservationDate(){

        List<BookedDate> findBookedDate = dynamicReservationRepository.findByReservationIdAndDate(1L, LocalDate.of(2020, 5, 20), LocalDate.of(2020, 5, 22));

        assertThat(findBookedDate.size()).isEqualTo(0);
    }

    private Accommodation setAccommodation(){
        Accommodation accommodation = new Accommodation();

        accommodation.setId(1L);
        accommodation.setCity("서울시");
        accommodation.setGu("강남구");

        return accommodation;
    }

    private Reservation setReservation(){
        Reservation reservation = Reservation.builder().checkInDate(LocalDate.of(2020, 2, 20)).checkoutDate(LocalDate.of(2020, 2, 22))
                .guestNum(3)
                .totalPrice(30000)
                .build();

        return reservation;
    }

}