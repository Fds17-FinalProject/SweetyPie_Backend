package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.BookedDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback
@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class DynamicReservationRepositoryTest {

    @Autowired
    private DynamicReservationRepository dynamicReservationRepository;

    @Test
    void duplicateReservationDate(){

        List<BookedDate> findBookedDate = dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2022, 2, 10), LocalDate.of(2022, 2, 12));

        assertThat(findBookedDate.size()).isEqualTo(2);
        assertThat(findBookedDate.get(0).getDate()).isEqualTo("2022-02-10");
        assertThat(findBookedDate.get(1).getDate()).isEqualTo("2022-02-11");
    }

    @Test
    void nonDuplicateReservationDate(){

        List<BookedDate> findBookedDate = dynamicReservationRepository.findByAccommodationIdAndDate(1L, LocalDate.of(2022, 5, 20), LocalDate.of(2022, 5, 22));

        assertThat(findBookedDate.size()).isEqualTo(0);
    }
}