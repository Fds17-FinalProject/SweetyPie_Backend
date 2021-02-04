package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Page<Accommodation> findAccommodationsBy(Pageable pageable);

    Page<Accommodation> findByCityContaining(String city, Pageable pageable);

    Page<Accommodation> findByCityContainingOrGuContaining(String city, String gu, Pageable pageable);

    Page<Accommodation> findByBuildingTypeContaining(String buildingType, Pageable pageable);
}