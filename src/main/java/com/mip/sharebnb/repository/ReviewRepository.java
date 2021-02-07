package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findReviewByAccommodation_IdAndMember_Id(long AccommodationId, long MemberId);
}
