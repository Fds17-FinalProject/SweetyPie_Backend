package com.mip.sharebnb.controller;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationRepository accommodationRepository;

    @GetMapping("/accommodation/{id}")
    public Accommodation getAccommodation(@PathVariable Long id) {

        return accommodationRepository.findById(id).orElse(Accommodation.emptyObject());
    }
}