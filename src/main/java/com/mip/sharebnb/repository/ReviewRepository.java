package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findReviewByAccommodation_IdAndMember_Id(long AccommodationId, long MemberId);
}
