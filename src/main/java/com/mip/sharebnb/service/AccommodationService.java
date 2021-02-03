package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.dynamic.DynamicAccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final DynamicAccommodationRepository dynamicAccommodationRepository;
    private final AccommodationRepository accommodationRepository;

    public AccommodationDto findById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElse(Accommodation.emptyObject());

        return new AccommodationDto(
                accommodation,
                accommodation.getReviews(),
                accommodation.getBookedDates(),
                accommodation.getAccommodationPictures()
        );
    }

    public Page<Accommodation> findByCityContaining(String searchKeyword, Pageable page) {

        return accommodationRepository.findByCityContaining(searchKeyword, page);
    }

    public Page<Accommodation> findByBuildingTypeContaining(String buildingType, Pageable page) {

        return accommodationRepository.findByBuildingTypeContaining(buildingType, page);
    }

    public Page<Accommodation> findByCityContainingOrGuContaining(String searchKeyword, Pageable page) {

        return accommodationRepository.findByCityContainingOrGuContaining(searchKeyword, searchKeyword, page);
    }

    public List<Accommodation> searchAccommodationsByQueryDsl(String searchKeyword,
                                                              LocalDate checkIn, LocalDate checkout,
                                                              int guestNum, Pageable page) {

        return dynamicAccommodationRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, page);
    }
}
