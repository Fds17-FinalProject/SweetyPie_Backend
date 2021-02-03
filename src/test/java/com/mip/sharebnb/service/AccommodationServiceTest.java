package com.mip.sharebnb.service;

import com.mip.sharebnb.repository.AccommodationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @InjectMocks
    private AccommodationService accommodationService;

    @Mock
    private AccommodationRepository accommodationRepository;

    @DisplayName("")
    @Test
    void findById() {

    }

    @DisplayName("")
    @Test
    void findByCityContaining() {
    }

    @DisplayName("")
    @Test
    void findByBuildingTypeContaining() {
    }

    @DisplayName("")
    @Test
    void findByCityContainingOrGuContaining() {
    }

    @DisplayName("")
    @Test
    void searchAccommodationsByQueryDsl() {
    }
}