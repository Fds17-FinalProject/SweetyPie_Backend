package com.mip.sharebnb.controller;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationRepository accommodationRepository;

    private final AccommodationService accommodationService;

    @GetMapping("/accommodation/{id}")
    public Accommodation getAccommodation(@PathVariable Long id) {

        return accommodationRepository.findById(id).orElse(Accommodation.emptyObject());
    }

    @GetMapping("/accommodation/desc/{id}")
    public String getAccommodationDesc(@PathVariable Long id) {

        return accommodationRepository.findById(id).orElse(Accommodation.emptyObject()).getAccommodationDesc();
    }

    @GetMapping("/accommodations")
    public List<Accommodation> getAllAccommodations() {

        return accommodationRepository.findAll();
    }

    @GetMapping("/accommodations/city/{city}")
    public Page<Accommodation> getAccommodationsByCity(@PathVariable String city, @RequestParam int page) {

        return accommodationService.findByCityContaining(city, page);
    }

    @GetMapping("/accommodations/search/{searchKeyword}")
    public Page<Accommodation> getAccommodationsBySearchKeyword(@PathVariable String searchKeyword, @RequestParam int page) {

        return accommodationService.findByCityContainingOrGuContaining(searchKeyword, page);
    }


    @GetMapping("/accommodations/search")
    public List<Accommodation> getAccommodationsBySearch(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkout,
            @RequestParam(defaultValue = "0") int guestNum, @RequestParam(defaultValue = "0") int page) {

        return accommodationService.searchAccommodationsByQueryDsl(searchKeyword, checkIn, checkout, guestNum, page);
    }
}