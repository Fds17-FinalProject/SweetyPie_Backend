package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    public Page<Accommodation> findByCityContaining(String searchKeyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return accommodationRepository.findByCityContaining(searchKeyword, pageable);
    }
}
