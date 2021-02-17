package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.SearchAccommodationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping("/accommodation/{id}")
    public AccommodationDto getAccommodation(@PathVariable Long id) {

        return accommodationService.findById(id);
    }

    @GetMapping("/accommodations")
    public Page<Accommodation> getAllAccommodations(@PageableDefault(size = 20) Pageable page) {

        return accommodationService.findAccommodations(page);
    }

    @GetMapping("/accommodations/city/{city}")
    public Page<Accommodation> getAccommodationsByCity(@PathVariable String city, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findByCityContaining(city, page);
    }

    @GetMapping("/accommodations/buildingType/{buildingType}")
    public Page<Accommodation> getAccommodationsByBuildingType(@PathVariable String buildingType, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findByBuildingTypeContaining(buildingType, page);
    }

    @GetMapping("/accommodations/search")
    public Page<SearchAccommodationDto> getAccommodationsBySearch(
            HttpServletRequest request, @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkout,
            @RequestParam(required = false, defaultValue = "1") int guestNum, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findAccommodationsBySearch(request, searchKeyword, checkIn, checkout, guestNum, page);
    }

    @GetMapping("/accommodations/mapSearch")
    public Page<SearchAccommodationDto> getAccommodationsByMapSearch(@RequestParam float minLatitude, @RequestParam float maxLatitude,
                                                            @RequestParam float minLongitude, @RequestParam float maxLongitude,
                                                            HttpServletRequest request, @PageableDefault(size = 20) Pageable page) {

        return accommodationService.findAccommodationsByMapSearch(request, minLatitude, maxLatitude, minLongitude, maxLongitude, page);
    }
}