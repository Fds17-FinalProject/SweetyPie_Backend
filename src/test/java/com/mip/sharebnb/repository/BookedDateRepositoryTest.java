package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.BookedDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class BookedDateRepositoryTest {

    @Autowired
    private BookedDateRepository bookedDateRepository;

    @Test
    void deleteByReservationId() {

        bookedDateRepository.deleteBookedDateByReservationId(1L);

        Optional<BookedDate> optionalBookedDate = bookedDateRepository.findById(1L);
        Optional<BookedDate> optionalBookedDate1 = bookedDateRepository.findById(2L);
        Optional<BookedDate> optionalBookedDate2 = bookedDateRepository.findById(3L);

        assertThat(optionalBookedDate.isPresent()).isFalse();
        assertThat(optionalBookedDate1.isPresent()).isFalse();
        assertThat(optionalBookedDate2.get().getDate()).isEqualTo(LocalDate.of(2022,2,20));
    }
}