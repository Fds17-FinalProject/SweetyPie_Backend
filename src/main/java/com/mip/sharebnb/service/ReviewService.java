package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Optional<Review> findReviewByAccommodation_IdAndMember_Id(long accommodationId, long memberId) {

        return reviewRepository.findReviewByAccommodation_IdAndMember_Id(accommodationId, memberId);
    }
}
