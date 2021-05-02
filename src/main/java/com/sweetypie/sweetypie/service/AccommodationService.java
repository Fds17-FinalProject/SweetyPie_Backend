package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.AccommodationDto;
import com.sweetypie.sweetypie.dto.IsBookmarkDto;
import com.sweetypie.sweetypie.dto.SearchAccommodationDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.InputNotValidException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        Page<Accommodation> accommodations = dynamicAccRepository.findByCity(city, parseTokenToMemberId(token), page);

        return mapToSearchAccommodationDtos(accommodations, page);
    }

    public Page<SearchAccommodationDto> findByBuildingType(String token, String buildingType, Pageable page) {

        Page<Accommodation> accommodations = dynamicAccRepository.findByBuildingType(buildingType, page);

        return mapToSearchAccommodationDtos(accommodations, page);
    }

    public Page<SearchAccommodationDto> findAccommodationsBySearch(String token, String searchKeyword,
                                                                   LocalDate checkIn, LocalDate checkout,
                                                                   int guestNum, Integer minPrice, Integer maxPrice, String types, Pageable page) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        Page<Accommodation> accommodations = dynamicAccRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, minPrice, maxPrice, types, page);
        List<IsBookmarkDto> bookmarks = dynamicAccRepository.findIsBookmarks(searchKeyword, checkIn, checkout, guestNum, parseTokenToMemberId(token), minPrice, maxPrice, types, page);

        return bookmarks == null ? mapToSearchAccommodationDtos(accommodations, page) : mapToSearchAccommodationDtos(accommodations, bookmarks, page);
    }

    public Page<SearchAccommodationDto> findAccommodationsByMapSearch(String token, Float minLatitude, Float maxLatitude,
                                                                      Float minLongitude, Float maxLongitude, Integer minPrice, Integer maxPrice,
                                                                      LocalDate checkIn, LocalDate checkout, int guestNum, String types, Pageable page) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        Page<Accommodation> accommodations = dynamicAccRepository.findAccommodationsByMapSearch(minLatitude, maxLatitude, minLongitude, maxLongitude,
                checkIn, checkout, minPrice, maxPrice, guestNum, types, page);

        List<IsBookmarkDto> bookmarks = dynamicAccRepository.findIsBookmarks(minLatitude, maxLatitude, minLongitude, maxLongitude,
                checkIn, checkout, minPrice, maxPrice, guestNum, parseTokenToMemberId(token), types, page);

        return bookmarks == null ? mapToSearchAccommodationDtos(accommodations, page) : mapToSearchAccommodationDtos(accommodations, bookmarks, page);
    }

    public List<Integer> findPricesBySearch(String searchKeyword, Float minLatitude, Float maxLatitude, Float minLongitude, Float maxLongitude,
                                            LocalDate checkIn, LocalDate checkout, int guestNum, String types) {

        checkIn = validateCheckInCheckout(checkIn, checkout);

        return dynamicAccRepository.findPricesBySearch(searchKeyword, minLatitude, maxLatitude, minLongitude, maxLongitude, checkIn, checkout, guestNum, types);
    }

    private AccommodationDto setListObjects(AccommodationDto acc) {

        acc.setAccommodationPictures(accommodationPictureRepository.findByAccommodationId(acc.getId()));
        acc.setBookedDateDtos(dynamicBookedDateRepository.findByAccommodationId(acc.getId()));
        acc.setReviews(reviewRepository.findReviewsByAccommodationId(acc.getId()));

        return acc;
    }

    private Long parseTokenToMemberId(String token) {
        Long memberId = null;

        if (token != null) {
            Member member = memberRepository.findById(tokenProvider.parseTokenToGetMemberId(token))
                    .orElseThrow(() -> new DataNotFoundException("Member Not Found"));

            memberId = member.getId();
        }

        return memberId;
    }

    private LocalDate validateCheckInCheckout(LocalDate checkIn, LocalDate checkout) {

        if (checkIn != null && checkout != null && checkout.isBefore(checkIn)) {
            throw new InputNotValidException("Checkout time is before check-in time");
        }

        if (checkout != null && checkout.isBefore(LocalDate.now())) {
            throw new InputNotValidException("Checkout time has passed");
        }

        if (checkIn != null && checkIn.isBefore(LocalDate.now())) {
            throw new InputNotValidException("Check-in time has passed");
        }

        if (checkIn == null) {
            checkIn = LocalDate.now();
        }

        return checkIn;
    }

    private Page<SearchAccommodationDto> mapToSearchAccommodationDtos(Page<Accommodation> accommodations, Pageable page) {
        List<Accommodation> accommodationList = accommodations.toList();
        List<SearchAccommodationDto> searchAccommodationDtos = new ArrayList<>();

        for (int i = 0; i < accommodationList.size(); i++) {
            searchAccommodationDtos.add(mapToSearchAccommodationDto(accommodationList.get(i)));
        }

        return new PageImpl<>(searchAccommodationDtos, page, accommodations.getTotalPages());
    }

    private Page<SearchAccommodationDto> mapToSearchAccommodationDtos(Page<Accommodation> accommodations, List<IsBookmarkDto> isBookmarkDtos, Pageable page) {
        List<Accommodation> accommodationList = accommodations.toList();
        List<SearchAccommodationDto> searchAccommodationDtos = new ArrayList<>();

        for (int i = 0; i < accommodationList.size(); i++) {
            searchAccommodationDtos.add(mapToSearchAccommodationDto(accommodationList.get(i), isBookmarkDtos.get(i)));
        }

        return new PageImpl<>(searchAccommodationDtos, page, accommodations.getTotalPages());
    }

    private SearchAccommodationDto mapToSearchAccommodationDto(Accommodation accommodation) {
        return SearchAccommodationDto.builder()
                .accommodationType(accommodation.getAccommodationType())
                .accommodationPictures(accommodation.getAccommodationPictures())
                .address(accommodation.getAddress())
                .bathroomNum(accommodation.getBathroomNum())
                .bedNum(accommodation.getBedNum())
                .bedroomNum(accommodation.getBedroomNum())
                .buildingType(accommodation.getBuildingType())
                .capacity(accommodation.getCapacity())
                .city(accommodation.getCity())
                .gu(accommodation.getGu())
                .contact(accommodation.getContact())
                .hostName(accommodation.getHostName())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .id(accommodation.getId())
                .price(accommodation.getPrice())
                .rating(accommodation.getRating())
                .reviewNum(accommodation.getReviewNum())
                .title(accommodation.getTitle())
                .isBookmarked(false)
                .build();
    }

    private SearchAccommodationDto mapToSearchAccommodationDto(Accommodation accommodation, IsBookmarkDto isBookmarkDto) {
        return SearchAccommodationDto.builder()
                .accommodationType(accommodation.getAccommodationType())
                .accommodationPictures(accommodation.getAccommodationPictures())
                .address(accommodation.getAddress())
                .bathroomNum(accommodation.getBathroomNum())
                .bedNum(accommodation.getBedNum())
                .bedroomNum(accommodation.getBedroomNum())
                .buildingType(accommodation.getBuildingType())
                .capacity(accommodation.getCapacity())
                .city(accommodation.getCity())
                .gu(accommodation.getGu())
                .contact(accommodation.getContact())
                .hostName(accommodation.getHostName())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .id(accommodation.getId())
                .price(accommodation.getPrice())
                .rating(accommodation.getRating())
                .reviewNum(accommodation.getReviewNum())
                .title(accommodation.getTitle())
                .isBookmarked(isBookmarkDto.isBookmark())
                .build();
    }
}