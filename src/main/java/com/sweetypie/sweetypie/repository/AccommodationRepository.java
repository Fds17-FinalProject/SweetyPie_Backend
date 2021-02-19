package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Page<Accommodation> findAccommodationsBy(Pageable pageable);

    Page<Accommodation> findByCityContainingOrderByRandId(String city, Pageable pageable);

    Page<Accommodation> findByCityContainingOrGuContainingOrderByRandId(String city, String gu, Pageable pageable);

    Page<Accommodation> findByBuildingTypeContainingOrderByRandId(String buildingType, Pageable pageable);
}