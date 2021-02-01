package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface AccommodationRepository extends CrudRepository<Accommodation, Long> {

    Page<Accommodation> findByCityContaining(String searchKeyword, Pageable pageable);
}
