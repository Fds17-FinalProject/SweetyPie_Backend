package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        List<Accommodation> accommodations = dynamicAccommodationRepository.findAccommodationsBySearch("서울", LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 5), 3, 2);

        assertThat(accommodations.size()).isEqualTo(10);

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getCity()).isEqualTo("서울특별시");
        }
    }

    @DisplayName("검색어 없이 메인 검색 테스트")
    @Test
    void searchIfSearchKeywordIsEmpty() {
        List<Accommodation> accommodations = dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 5), 3, 2);

        assertThat(accommodations.size()).isEqualTo(10);

        for (Accommodation accommodation : accommodations) {
            assertThat(accommodation.getCapacity()).isGreaterThanOrEqualTo(3);
        }
    }

    @DisplayName("인원 수 없이 메인 검색 테스트")
    @Test
    void searchIfGuestNumIsEmpty() {
    }

    private void givenAccommodation() {
        Accommodation accommodation = new Accommodation(1L, "서울특별시", "마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", "36.141", "126.531", "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", "4.56", 125, "전체", "원룸", "이재복", 543, null, null, null, null, null);

        accommodationRepository.save(accommodation);
    }
}