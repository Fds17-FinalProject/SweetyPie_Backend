package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.AccommodationPicture;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.dynamic.DynamicAccommodationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @InjectMocks
    private AccommodationService accommodationService;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private DynamicAccommodationRepository dynamicAccommodationRepository;

    @DisplayName("도시별 검색")
    @Test
    void findByCityContaining() {
        when(accommodationRepository.findByCityContainingOrderByRandId("서울", PageRequest.of(1, 10))).thenReturn(mockAccommodationPage());

        Page<Accommodation> accommodations = accommodationService.findByCityContaining("서울", PageRequest.of(1, 10));

        assertThat(accommodations.getSize()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getCity()).isEqualTo("서울특별시");
    }

    @DisplayName("건물 유형별 검색")
    @Test
    void findByBuildingTypeContaining() {
        when(accommodationRepository.findByBuildingTypeContainingOrderByRandId("원룸", PageRequest.of(1, 10))).thenReturn(mockAccommodationPage());

        Page<Accommodation> accommodations = accommodationService.findByBuildingTypeContaining("원룸", PageRequest.of(1, 10));

        assertThat(accommodations.getSize()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getBuildingType()).isEqualTo("원룸");
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");

    }

    @DisplayName("메인 검색 (지역, 인원)")
    @Test
    void searchAccommodationsByQueryDsl1() {
        when(dynamicAccommodationRepository
                .findAccommodationsBySearch("서울", LocalDate.now(), null, 1, PageRequest.of(1, 10)))
                .thenReturn(mockAccommodationPage());

        Page<Accommodation> accommodations = accommodationService
                .findAccommodationsBySearch("서울", null, null, 1, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getBuildingType()).isEqualTo("원룸");
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    @DisplayName("메인 검색 (체크인, 체크아웃)")
    @Test
    void searchAccommodationsByQueryDsl2() {
        when(dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2022, 3, 5)
                , LocalDate.of(2022, 3, 10), 0, PageRequest.of(1, 10)))
                .thenReturn(mockAccommodationPage());

        Page<Accommodation> accommodations = accommodationService.findAccommodationsBySearch(null, LocalDate.of(2022, 3, 5),
                LocalDate.of(2022, 3, 10), 0, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    @DisplayName("메인 검색 (체크인만)")
    @Test
    void searchAccommodationsByQueryDsl4() {
        when(dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2022, 5, 1),
                null, 0, PageRequest.of(1, 10))).thenReturn(mockAccommodationPage());

        Page<Accommodation> accommodations = accommodationService.findAccommodationsBySearch(null, LocalDate.of(2022, 5, 1), null, 0, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    private Accommodation mockAccommodation(Long id) {
        Accommodation accommodation = new Accommodation(id, 0, "서울특별시", "마포구", "서울특별시 마포구 독막로 266", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null);
        List<AccommodationPicture> accommodationPictures = new ArrayList<>();

        accommodationPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/1.jpg"));
        accommodationPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/2.jpg"));
        accommodationPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/3.jpg"));
        accommodationPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/4.jpg"));
        accommodationPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/5.jpg"));
        accommodation.setAccommodationPictures(accommodationPictures);

        return accommodation;
    }

    private List<Accommodation> mockAccommodationList() {
        List<Accommodation> accommodations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            accommodations.add(mockAccommodation((long) i + 1));
        }

        return accommodations;
    }

    private Page<Accommodation> mockAccommodationPage() {

        return new PageImpl<>(mockAccommodationList());
    }

    private AccommodationDto mappingAccommodationDto(Accommodation accommodation) {

        return new AccommodationDto(accommodation.getCity(),
                accommodation.getGu(), accommodation.getAddress(), accommodation.getTitle(),
                accommodation.getBathroomNum(), accommodation.getBedroomNum(),
                accommodation.getBedNum(), accommodation.getCapacity(),
                accommodation.getPrice(), accommodation.getContact(),
                accommodation.getLatitude(), accommodation.getLongitude(),
                accommodation.getLocationDesc(), accommodation.getTransportationDesc(),
                accommodation.getAccommodationDesc(), accommodation.getRating(),
                accommodation.getReviewNum(), accommodation.getAccommodationType(),
                accommodation.getBuildingType(), accommodation.getHostName(),
                accommodation.getHostDesc(), accommodation.getHostReviewNum(),
                accommodation.getReviews(), accommodation.getBookedDates(),
                accommodation.getAccommodationPictures());
    }
}