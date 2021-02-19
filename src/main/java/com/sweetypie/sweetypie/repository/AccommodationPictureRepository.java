package com.sweetypie.sweetypie.repository;

import com.sweetypie.sweetypie.model.AccommodationPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationPictureRepository extends JpaRepository<AccommodationPicture, Long> {

    List<AccommodationPicture> findByAccommodationId(long accommodationId);
}
