package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.SearchAccommodationDto;
import com.mip.sharebnb.model.AccommodationPicture;
import com.mip.sharebnb.repository.AccommodationPictureRepository;
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
    private DynamicAccommodationRepository dynamicAccommodationRepository;

    @Mock
    private AccommodationPictureRepository accPictureRepository;

    @DisplayName("도시별 검색")
    @Test
    void findByCity() {
        when(dynamicAccommodationRepository.findByCity("서울", null, PageRequest.of(1, 10))).thenReturn(mockAccommodationPage());
        when(accPictureRepository.findByAccommodationId(1)).thenReturn(mockAccPictures());

        Page<SearchAccommodationDto> accommodations = accommodationService.findByCity(null, "서울", PageRequest.of(1, 10));

        assertThat(accommodations.getSize()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getCity()).isEqualTo("서울특별시");
    }

    @DisplayName("건물 유형별 검색")
    @Test
    void findByBuildingType() {
        when(dynamicAccommodationRepository.findByBuildingType("아파트", null, PageRequest.of(1, 10))).thenReturn(mockAccommodationPage());
        when(accPictureRepository.findByAccommodationId(1)).thenReturn(mockAccPictures());

        Page<SearchAccommodationDto> accommodations = accommodationService.findByBuildingType(null, "아파트", PageRequest.of(1, 10));

        assertThat(accommodations.getSize()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getBuildingType()).isEqualTo("아파트");
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    @DisplayName("메인 검색 (지역, 인원)")
    @Test
    void searchAccommodationsByQueryDsl1() {
        when(dynamicAccommodationRepository
                .findAccommodationsBySearch("서울", LocalDate.now(), null, 1, null, null, PageRequest.of(1, 10)))
                .thenReturn(mockAccommodationPage());
        when(accPictureRepository.findByAccommodationId(1)).thenReturn(mockAccPictures());

        Page<SearchAccommodationDto> accommodations = accommodationService
                .findAccommodationsBySearch(null, "서울", null, null, 1, null, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getBuildingType()).isEqualTo("아파트");
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    @DisplayName("메인 검색 (체크인, 체크아웃)")
    @Test
    void searchAccommodationsByQueryDsl2() {
        when(dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2022, 3, 5)
                , LocalDate.of(2022, 3, 10), 0, null, null, PageRequest.of(1, 10)))
                .thenReturn(mockAccommodationPage());
        when(accPictureRepository.findByAccommodationId(1)).thenReturn(mockAccPictures());

        Page<SearchAccommodationDto> accommodations = accommodationService.findAccommodationsBySearch(null, null, LocalDate.of(2022, 3, 5),
                LocalDate.of(2022, 3, 10), 0, null, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    @DisplayName("메인 검색 (체크인만)")
    @Test
    void searchAccommodationsByQueryDsl4() {
        when(dynamicAccommodationRepository.findAccommodationsBySearch(null, LocalDate.of(2022, 5, 1),
                null, 0, null, null, PageRequest.of(1, 10))).thenReturn(mockAccommodationPage());
        when(accPictureRepository.findByAccommodationId(1)).thenReturn(mockAccPictures());

        Page<SearchAccommodationDto> accommodations = accommodationService
                .findAccommodationsBySearch(null, null, LocalDate.of(2022, 5, 1),
                        null, 0, null, PageRequest.of(1, 10));

        assertThat(accommodations.toList().size()).isEqualTo(10);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().size()).isEqualTo(5);
        assertThat(accommodations.toList().get(0).getAccommodationPictures().get(0).getUrl()).isEqualTo("https://sharebnb.co.kr/pictures/1.jpg");
    }

    private SearchAccommodationDto mockSearchAccommodationDto(Long id) {
        SearchAccommodationDto searchAccommodationDto =
                new SearchAccommodationDto(id, "서울특별시", "마포구", "서울특별시 마포구 독막로 266", "살기 좋은 곳",
                        1, 1, 1, 40000, 2, "010-1234-5678", 36.141f,
                        126.531f, 4.56f, 256, "전체", "아파트", "이재복", null);

        return searchAccommodationDto;
    }

    private List<AccommodationPicture> mockAccPictures() {
        List<AccommodationPicture> accPictures = new ArrayList<>();

        accPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/1.jpg"));
        accPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/2.jpg"));
        accPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/3.jpg"));
        accPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/4.jpg"));
        accPictures.add(new AccommodationPicture("https://sharebnb.co.kr/pictures/5.jpg"));

        return accPictures;
    }


    private List<SearchAccommodationDto> mockAccommodationList() {
        List<SearchAccommodationDto> accommodations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            accommodations.add(mockSearchAccommodationDto((long) i + 1));
        }

        return accommodations;
    }

    private Page<SearchAccommodationDto> mockAccommodationPage() {

        return new PageImpl<>(mockAccommodationList());
    }
}