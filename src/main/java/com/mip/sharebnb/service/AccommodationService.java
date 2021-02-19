package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.SearchAccommodationDto;
import com.mip.sharebnb.exception.DataNotFoundException;
import com.mip.sharebnb.exception.InvalidInputException;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationPictureRepository;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import com.mip.sharebnb.repository.dynamic.DynamicAccommodationRepository;
import com.mip.sharebnb.repository.dynamic.DynamicBookedDateRepository;
import com.mip.sharebnb.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Transactional
@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationPictureRepository accommodationPictureRepository;
    private final DynamicBookedDateRepository dynamicBookedDateRepository;
    private final DynamicAccommodationRepository dynamicAccRepository;
    private final AccommodationRepository accRepository;
    private final ReviewRepository reviewRepository;
    private final TokenProvider tokenProvider;

    public AccommodationDto findById(HttpServletRequest request, Long id) {
        AccommodationDto accommodationDto = dynamicAccRepository.findById(parseRequestToMemberId(request), id);

        if (accommodationDto == null) {
            throw new DataNotFoundException("Accommodation Not Found");
        }

        return setListObjects(accommodationDto);
    }

    public Page<Accommodation> findAccommodations(Pageable pageable) {

        return accRepository.findAccommodationsBy(pageable);
    }

    public Page<SearchAccommodationDto> findByCity(HttpServletRequest request, String city, Pageable page) {

        return setPictures(dynamicAccRepository.findByCity(city, parseRequestToMemberId(request), page));
    }

    public Page<SearchAccommodationDto> findByBuildingType(HttpServletRequest request, String buildingType, Pageable page) {

        return setPictures(dynamicAccRepository.findByBuildingType(buildingType, parseRequestToMemberId(request), page));
    }

    public Page<SearchAccommodationDto> findAccommodationsBySearch(HttpServletRequest request, String searchKeyword,
                                                                   LocalDate checkIn, LocalDate checkout,
                                                                   int guestNum, String types, Pageable page) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        return setPictures(dynamicAccRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, parseRequestToMemberId(request), types, page));
    }

    public Page<SearchAccommodationDto> findAccommodationsByMapSearch(HttpServletRequest request, float minLatitude, float maxLatitude,
                                                                      float minLongitude, float maxLongitude,
                                                                      LocalDate checkIn, LocalDate checkout, int guestNum, String types, Pageable page) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        return setPictures(dynamicAccRepository.findAccommodationsByMapSearch(minLatitude, maxLatitude, minLongitude, maxLongitude,
                checkIn, checkout, guestNum, parseRequestToMemberId(request), types, page));
    }

    private AccommodationDto setListObjects(AccommodationDto acc) {

        acc.setAccommodationPictures(accommodationPictureRepository.findByAccommodationId(acc.getId()));
        acc.setBookedDateDtos(dynamicBookedDateRepository.findByAccommodationId(acc.getId()));
        acc.setReviews(reviewRepository.findReviewsByAccommodationId(acc.getId()));

        return acc;
    }

    private Page<SearchAccommodationDto> setPictures(Page<SearchAccommodationDto> searchAccommodationDtos) {

        for (SearchAccommodationDto acc : searchAccommodationDtos) {
            acc.setAccommodationPictures(accommodationPictureRepository.findByAccommodationId(acc.getId()));
        }

        return searchAccommodationDtos;
    }

    private Long parseRequestToMemberId(HttpServletRequest request) {
        Long memberId = null;

        if (request != null) {
            String token = request.getHeader("Authorization");

            if (token != null) {
                memberId = tokenProvider.parseTokenToGetUserId(token);
            }
        }

        return memberId;
    }

    private LocalDate validateCheckInCheckout(LocalDate checkIn, LocalDate checkout) {

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

        return checkIn;
    }
}