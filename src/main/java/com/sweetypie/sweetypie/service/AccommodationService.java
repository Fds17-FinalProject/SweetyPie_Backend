package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.AccommodationDto;
import com.sweetypie.sweetypie.dto.SearchAccommodationDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.InvalidInputException;
import com.sweetypie.sweetypie.model.Accommodation;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.repository.AccommodationPictureRepository;
import com.sweetypie.sweetypie.repository.AccommodationRepository;
import com.sweetypie.sweetypie.repository.MemberRepository;
import com.sweetypie.sweetypie.repository.ReviewRepository;
import com.sweetypie.sweetypie.repository.dynamic.DynamicAccommodationRepository;
import com.sweetypie.sweetypie.repository.dynamic.DynamicBookedDateRepository;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
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

    private final AccommodationPictureRepository accommodationPictureRepository;
    private final DynamicBookedDateRepository dynamicBookedDateRepository;
    private final DynamicAccommodationRepository dynamicAccRepository;
    private final MemberRepository memberRepository;
    private final AccommodationRepository accRepository;
    private final ReviewRepository reviewRepository;
    private final TokenProvider tokenProvider;

    public AccommodationDto findById(String token, Long id) {

        AccommodationDto accommodationDto = dynamicAccRepository.findById(parseTokenToMemberId(token), id);

        if (accommodationDto == null) {
            throw new DataNotFoundException("Accommodation Not Found");
        }

        return setListObjects(accommodationDto);
    }

    public Page<Accommodation> findAccommodations(Pageable pageable) {

        return accRepository.findAccommodationsBy(pageable);
    }

    public Page<SearchAccommodationDto> findByCity(String token, String city, Pageable page) {

        return setPictures(dynamicAccRepository.findByCity(city, parseTokenToMemberId(token), page));
    }

    public Page<SearchAccommodationDto> findByBuildingType(String token, String buildingType, Pageable page) {

        return setPictures(dynamicAccRepository.findByBuildingType(buildingType, parseTokenToMemberId(token), page));
    }

    public Page<SearchAccommodationDto> findAccommodationsBySearch(String token, String searchKeyword,
                                                                   LocalDate checkIn, LocalDate checkout,
                                                                   int guestNum, String types, Pageable page) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        return setPictures(dynamicAccRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, parseTokenToMemberId(token), types, page));
    }

    public Page<SearchAccommodationDto> findAccommodationsByMapSearch(String token, float minLatitude, float maxLatitude,
                                                                      float minLongitude, float maxLongitude,
                                                                      LocalDate checkIn, LocalDate checkout, int guestNum, String types, Pageable page) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        return setPictures(dynamicAccRepository.findAccommodationsByMapSearch(minLatitude, maxLatitude, minLongitude, maxLongitude,
                checkIn, checkout, guestNum, parseTokenToMemberId(token), types, page));
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

    private Long parseTokenToMemberId(String token) {
        Long memberId = null;

        if (token != null) {
            Member member = memberRepository.findById(tokenProvider.parseTokenToGetUserId(token))
                    .orElseThrow(() -> new DataNotFoundException("Member Not Found"));
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