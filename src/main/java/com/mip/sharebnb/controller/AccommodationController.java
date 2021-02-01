package com.mip.sharebnb.controller;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/accommodations")
    public List<Accommodation> getAllAccommodations() {

        return (List<Accommodation>) accommodationRepository.findAll();
    }

    @GetMapping("/accommodations/search")
    public Page<Accommodation> searchAccommodations(@RequestParam String searchKeyword, @RequestParam int page) {

        return accommodationService.findByCityContaining(searchKeyword, page);
    }


}