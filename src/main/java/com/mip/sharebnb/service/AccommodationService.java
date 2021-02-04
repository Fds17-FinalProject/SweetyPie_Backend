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
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElse(Accommodation.emptyObject());

        return mappingAccommodationDto(accommodation);
    }

    public Page<Accommodation> findByCityContaining(String searchKeyword, Pageable page) {

        return accommodationRepository.findByCityContaining(searchKeyword, page);
    }

    public Page<Accommodation> findByBuildingTypeContaining(String buildingType, Pageable page) {

        return accommodationRepository.findByBuildingTypeContaining(buildingType, page);
    }

    public List<Accommodation> searchAccommodationsByQueryDsl(String searchKeyword,
                                                              LocalDate checkIn, LocalDate checkout,
                                                              int guestNum, Pageable page) {

        return dynamicAccommodationRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, page);
    }

    private AccommodationDto mappingAccommodationDto(Accommodation accommodation) {

        return new AccommodationDto(accommodation.getCity(),
                accommodation.getGu(), accommodation.getTitle(),
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
