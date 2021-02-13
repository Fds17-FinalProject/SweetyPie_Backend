package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
class AccommodationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @DisplayName("도시 별로 검색")
    @Test
    void findByCityContaining() {
        givenAccommodation();

        Pageable pageable = PageRequest.of(0, 1);
        Accommodation accommodation = accommodationRepository.findByCityContainingOrderByRandId("서울", pageable).toList().get(0);

        assertThat(accommodation.getCity()).isEqualTo("서울특별시");
        assertThat(accommodation.getGu()).isEqualTo("마포구");
        assertThat(accommodation.getLocationDesc()).isEqualTo("마포");
    }

    @DisplayName("도시 or 구로 검색")
    @Test
    void findByCityContainingOrGuContaining() {

        givenAccommodation();

        Pageable pageable = PageRequest.of(0, 1);
        List<Accommodation> accommodations = accommodationRepository.findByCityContainingOrGuContainingOrderByRandId("서울", "서울", pageable).toList();

        assertThat(accommodations.size()).isEqualTo(1);
        assertThat(accommodations.get(0).getCity()).isEqualTo("서울특별시");
        assertThat(accommodations.get(0).getGu()).isEqualTo("마포구");
        assertThat(accommodations.get(0).getLocationDesc()).isEqualTo("마포");
    }

    private void givenAccommodation() {
        Accommodation accommodation = new Accommodation(1L, 0, "서울특별시", "마포구", "서울특별시 마포구 독막로 266", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null);

        accommodationRepository.save(accommodation);
    }
}