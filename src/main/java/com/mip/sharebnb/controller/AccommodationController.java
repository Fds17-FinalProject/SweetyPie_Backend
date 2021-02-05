package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @Transactional
    @GetMapping("/accommodation/{id}")
    public AccommodationDto getAccommodation(@PathVariable Long id) {

        return accommodationService.findById(id);
    }

    @GetMapping("/accommodations")
    public Page<Accommodation> getAllAccommodations(@PageableDefault Pageable page) {

        return accommodationService.findAccommodations(page);
    }

    @GetMapping("/accommodations/city/{city}")
    public Page<Accommodation> getAccommodationsByCity(@PathVariable String city, @PageableDefault Pageable page) {

        return accommodationService.findByCityContaining(city, page);
    }

    @GetMapping("/accommodations/buildingType/{buildingType}")
    public Page<Accommodation> getAccommodationsByBuildingType(@PathVariable String buildingType, @PageableDefault Pageable page) {

        return accommodationService.findByBuildingTypeContaining(buildingType, page);
    }

    @GetMapping("/accommodations/search")
    public Page<Accommodation> getAccommodationsBySearch(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkout,
            @RequestParam(required = false, defaultValue = "0") int guestNum, @PageableDefault Pageable page) {

        return accommodationService.findAccommodationsBySearch(searchKeyword, checkIn, checkout, guestNum, page);
    }

    @GetMapping("/accommodations/mapSearch")
    public Page<Accommodation> getAccommodationsByMapSearch(Float minLatitude, Float maxLatitude,
                                                            Float minLongitude, Float maxLongitude,
                                                            @PageableDefault Pageable page) {

        return accommodationService.findAccommodationsByMapSearch(minLatitude, maxLatitude, minLongitude, maxLongitude, page);
    }
}