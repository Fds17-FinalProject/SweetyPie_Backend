package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.AccommodationPicture;
import com.mip.sharebnb.repository.AccommodationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @InjectMocks
    private AccommodationService accommodationService;

    @Mock
    private AccommodationRepository accommodationRepository;

    @DisplayName("도시별 검색")
    @Test
    void findByCityContaining() {
    }

    @DisplayName("")
    @Test
    void findByBuildingTypeContaining() {
    }

    @DisplayName("")
    @Test
    void findByCityContainingOrGuContaining() {
    }

    @DisplayName("")
    @Test
    void searchAccommodationsByQueryDsl() {
    }

    private AccommodationDto mockAccommodationDto() {
        List<AccommodationPicture> accommodationPictures = new ArrayList<>();
        accommodationPictures.add(new AccommodationPicture(1L, "https://sharebnb.co.kr/pictures/1.jpg"));
        accommodationPictures.add(new AccommodationPicture(2L, "https://sharebnb.co.kr/pictures/2.jpg"));
        accommodationPictures.add(new AccommodationPicture(3L, "https://sharebnb.co.kr/pictures/3.jpg"));
        accommodationPictures.add(new AccommodationPicture(4L, "https://sharebnb.co.kr/pictures/4.jpg"));
        accommodationPictures.add(new AccommodationPicture(5L, "https://sharebnb.co.kr/pictures/5.jpg"));

        return new AccommodationDto(mockAccommodation(1L), null, null, accommodationPictures);
    }

    private Accommodation mockAccommodation(Long id) {

        return new Accommodation(id, "서울특별시", "마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", "36.141", "126.531", "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", "4.56", 125, "전체", "원룸", "이재복", 543, null, null, null, null, null);
    }

    private List<Accommodation> mockAccommodationList() {
        List<Accommodation> accommodations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            accommodations.add(mockAccommodation((long) i));
        }

        return accommodations;
    }

    private Page<Accommodation> mockAccommodationPage() {

        return new PageImpl<>(mockAccommodationList());
    }
}