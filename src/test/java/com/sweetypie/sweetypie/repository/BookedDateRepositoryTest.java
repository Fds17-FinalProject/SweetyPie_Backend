package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.BookedDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(properties = "spring.config.location=classpath:test.yml")
@ExtendWith(SpringExtension.class)
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