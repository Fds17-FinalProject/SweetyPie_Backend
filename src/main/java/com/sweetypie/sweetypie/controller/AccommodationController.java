package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.aspect.LogExecutionTime;
import com.sweetypie.sweetypie.dto.AccommodationDto;
import com.sweetypie.sweetypie.dto.SearchAccommodationDto;
import com.sweetypie.sweetypie.model.Accommodation;
import com.sweetypie.sweetypie.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping("/accommodation/{id}")
    public AccommodationDto getAccommodation(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {

        return accommodationService.findById(token, id);
    }

    @GetMapping("/accommodations")
    public Page<Accommodation> getAllAccommodations(@PageableDefault(size = 20) Pageable page) {

        return accommodationService.findAccommodations(page);
    }

    @GetMapping("/accommodations/city/{city}")
    public Page<SearchAccommodationDto> getAccommodationsByCity(@RequestHeader(value = "Authorization", required = false) String token,
                                                                @PathVariable String city, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findByCity(token, city, page);
    }

    @GetMapping("/accommodations/buildingType/{buildingType}")
    public Page<SearchAccommodationDto> getAccommodationsByBuildingType(@RequestHeader(value = "Authorization", required = false) String token,
                                                                        @PathVariable String buildingType, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findByBuildingType(token, buildingType, page);
    }

    @LogExecutionTime
    @GetMapping("/accommodations/search")
    public Page<SearchAccommodationDto> getAccommodationsBySearch(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam(required = false) String searchKeyword,
                                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkout,
                                                                  @RequestParam(required = false, defaultValue = "1") int guestNum, @RequestParam(required = false) String types,
                                                                  @RequestParam(required = false) Integer minPrice, @RequestParam(required = false) Integer maxPrice,
                                                                  @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findAccommodationsBySearch(token, searchKeyword, checkIn, checkout, guestNum, minPrice, maxPrice, types, page);
    }

    @GetMapping("/accommodations/mapSearch")
    public Page<SearchAccommodationDto> getAccommodationsByMapSearch(@RequestParam float minLatitude, @RequestParam float maxLatitude,
                                                                     @RequestParam float minLongitude, @RequestParam float maxLongitude,
                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkout,
                                                                     @RequestParam(required = false, defaultValue = "1") int guestNum, @RequestParam(required = false) String types,
                                                                     @RequestParam(required = false) Integer minPrice, @RequestParam(required = false) Integer maxPrice,
                                                                     @RequestHeader(value = "Authorization", required = false) String token, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findAccommodationsByMapSearch(token, minLatitude, maxLatitude, minLongitude,
                maxLongitude, minPrice, maxPrice, checkIn, checkout, guestNum, types, page);
    }

    @GetMapping("/accommodations/price")
    public List<Integer> getAccommodationsPrices(@RequestParam(required = false) String searchKeyword,
                                                 @RequestParam(required = false) Float minLatitude, @RequestParam(required = false) Float maxLatitude,
                                                 @RequestParam(required = false) Float minLongitude, @RequestParam(required = false) Float maxLongitude,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkout,
                                                 @RequestParam(required = false, defaultValue = "1") int guestNum, @RequestParam(required = false) String types) {

        return accommodationService.findPricesBySearch(searchKeyword, minLatitude, maxLatitude, minLongitude, maxLongitude, checkIn, checkout, guestNum, types);
    }
}