package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.dynamic.DynamicAccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final DynamicAccommodationRepository dynamicAccommodationRepository;
    private final AccommodationRepository accommodationRepository;

    public Page<Accommodation> findByCityContaining(String searchKeyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return accommodationRepository.findByCityContaining(searchKeyword, pageable);
    }

    public Page<Accommodation> findByCityContainingOrGuContaining(String searchKeyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return accommodationRepository.findByCityContainingOrGuContaining(searchKeyword, searchKeyword, pageable);
    }

    public List<Accommodation> searchAccommodationsByQueryDsl(String searchKeyword,
                                                              LocalDate checkIn, LocalDate checkout,
                                                              int page) {

        return dynamicAccommodationRepository.findAccommodationsBySearch(searchKeyword, checkIn, checkout, page);
    }
}
