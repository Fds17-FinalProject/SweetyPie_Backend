package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.exception.DataNotFoundException;
import com.mip.sharebnb.exception.InvalidInputException;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.dynamic.DynamicAccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final DynamicAccommodationRepository dynamicAccommodationRepository;
    private final AccommodationRepository accommodationRepository;

    public AccommodationDto findById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Accommodation Not Found"));

        return mappingAccommodationDto(accommodation);
    }

    public Page<Accommodation> findAccommodations(Pageable pageable) {

        return accommodationRepository.findAccommodationsBy(pageable);
    }

    public Page<Accommodation> findByCityContaining(String searchKeyword, Pageable page) {

        return accommodationRepository.findByCityContaining(searchKeyword, page);
    }

    public Page<Accommodation> findByBuildingTypeContaining(String buildingType, Pageable page) {

        return accommodationRepository.findByBuildingTypeContaining(buildingType, page);
    }

    public Page<Accommodation> findAccommodationsBySearch(String searchKeyword,
                                                          LocalDate checkIn, LocalDate checkout,
                                                          int guestNum, Pageable page) {

        if (checkIn != null && checkout != null && checkout.isBefore(checkIn)) {
            throw new InvalidInputException("Checkout time is before check-in time");
        }

        if (checkout != null && checkout.isBefore(LocalDate.now())) {
            throw new InvalidInputException("Checkout time has passed");
        }

        if (checkIn != null && checkIn.isBefore(LocalDate.now())) {
            throw new InvalidInputException("Check-in time has passed");
        }

        if (checkIn == null) {
            checkIn = LocalDate.now();
        }

        return dynamicAccommodationRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, page);
    }

    public Page<Accommodation> findAccommodationsByMapSearch(float minLatitude, float maxLatitude,
                                                             float minLongitude, float maxLongitude, Pageable page) {

        return dynamicAccommodationRepository.findAccommodationsByMapSearch(minLatitude, maxLatitude, minLongitude, maxLongitude, page);
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
