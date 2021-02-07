package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.BookedDateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback
@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class DynamicAccommodationRepositoryTest {

    @Autowired
    private DynamicAccommodationRepository dynamicAccommodationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private BookedDateRepository bookedDateRepository;

    @DisplayName("메인 검색 테스트")
    @Test
    void search() {
        for (int i = 0; i < 10; i++) {
            Accommodation accommodation = givenAccommodation();
            accommodationRepository.save(accommodation);

            List<BookedDate> bookedDates = givenBookDates(accommodation);

            for (BookedDate bookedDate : bookedDates) {
                bookedDateRepository.save(bookedDate);
            }
        }

        Page<Accommodation> accommodations = dynamicAccommodationRepository.
                findAccommodationsBySearch("대구",
                        LocalDate.of(2022, 3, 3),
                        LocalDate.of(2022, 3, 4), 1,
                        PageRequest.of(0, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);

        if (!accommodations.isEmpty()) {
            for (Accommodation ac : accommodations) {
                assertThat(ac.getCity()).isEqualTo("대구광역시");
            }
        }
    }

    @DisplayName("메인 검색 (날짜 겹침) 테스트")
    @Test
    void search2() {
        for (int i = 0; i < 10; i++) {
            Accommodation accommodation = givenAccommodation();
            accommodationRepository.save(accommodation);

            List<BookedDate> bookedDates = givenBookDates(accommodation);

            for (BookedDate bookedDate : bookedDates) {
                bookedDateRepository.save(bookedDate);
            }
        }

        Page<Accommodation> accommodations = dynamicAccommodationRepository.
                findAccommodationsBySearch("대구",
                        LocalDate.of(2022, 3, 3),
                        LocalDate.of(2022, 3, 7), 1,
                        PageRequest.of(0, 10));

        assertThat(accommodations.toList().size()).isEqualTo(0);

        if (!accommodations.isEmpty()) {
            for (Accommodation ac : accommodations) {
                assertThat(ac.getCity()).isEqualTo("대구광역시");
            }
        }
    }

    @DisplayName("검색어 없이 메인 검색 테스트")
    @Test
    void searchIfSearchKeywordIsEmpty() {
        Page<Accommodation> accommodations = dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 5), 3, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getCapacity()).isGreaterThanOrEqualTo(3);
        }
    }

    @DisplayName("지도 범위(좌표 기준) 내 검색")
    @Test
    void searchByCoordinate() {
        Page<Accommodation> accommodations = dynamicAccommodationRepository.findAccommodationsByMapSearch(37f, 37.5f, 126f, 127f, PageRequest.of(1, 10));

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getLatitude()).isGreaterThan(37f);
            assertThat(accommodation.getLatitude()).isLessThan(37.5f);
            assertThat(accommodation.getLongitude()).isGreaterThan(126f);
            assertThat(accommodation.getLongitude()).isLessThan(127f);
        }
    }

    private Accommodation givenAccommodation() {
        Accommodation accommodation = new Accommodation(null, "대구광역시", "수성구", "대구광역시 수성구 xx로", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null);

        return accommodation;
    }

    private List<BookedDate> givenBookDates(Accommodation accommodation) {
        Reservation reservation = new Reservation();

        List<BookedDate> bookedDates = new ArrayList<>();
        bookedDates.add(new BookedDate(null, LocalDate.of(2022, 3, 4), accommodation, reservation));
        bookedDates.add(new BookedDate(null, LocalDate.of(2022, 3, 5), accommodation, reservation));
        bookedDates.add(new BookedDate(null, LocalDate.of(2022, 3, 6), accommodation, reservation));
        bookedDates.add(new BookedDate(null, LocalDate.of(2022, 3, 7), accommodation, reservation));
        bookedDates.add(new BookedDate(null, LocalDate.of(2022, 3, 8), accommodation, reservation));

        return bookedDates;
    }
}