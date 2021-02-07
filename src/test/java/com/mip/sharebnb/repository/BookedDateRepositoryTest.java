package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class BookedDateRepositoryTest {

    @Autowired
    private BookedDateRepository bookedDateRepository;

    @Test
    void deleteByAccommodationIdAndDateIn() {
        // 테스트를 어떻게 해봐야 할지 잘 모르겠음.. 일단 DB에서 눈으로 값이 삭제되는지 직접 확인함.
        bookedDateRepository.deleteBookedDateByAccommodationIdAndDateIn(1L, mockBookedDate());

    }

    private List<LocalDate> mockBookedDate() {

        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(LocalDate.of(2020, 2, 20));
        localDates.add(LocalDate.of(2020, 2, 21));

        return localDates;

    }
}