package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

    @DisplayName("메인 검색 테스트")
    @Test
    void search() {

        for (int i = 0; i < 10; i++) {
            accommodationRepository.save(givenAccommodation());
        }

        Page<Accommodation> accommodations = dynamicAccommodationRepository.
                findAccommodationsBySearch("강릉",
                        LocalDate.of(2021, 5, 1),
                        LocalDate.of(2021, 5, 5), 3,
                        PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getCity()).isEqualTo("강릉시");
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

    @DisplayName("인원 수 없이 메인 검색 테스트")
    @Test
    void searchIfGuestNumIsEmpty() {

    }

    private Accommodation givenAccommodation() {
        return new Accommodation(null, "서울특별시", "마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", "36.141", "126.531", "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", "4.56", 125, "전체", "원룸", "이재복", 543, null, null, null, null, null);
    }
}