package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final AccommodationRepository accommodationRepository;

    private final MemberRepository memberRepository;

    public Optional<Review> findReviewByAccommodation_IdAndMember_Id(long accommodationId, long memberId) {

        return reviewRepository.findReviewByAccommodation_IdAndMember_Id(accommodationId, memberId);
    }

    public void writeReview(ReviewDto reviewDto) throws NotFoundException {
        Review review = Review.builder()
                .content(reviewDto.getContent())
                .createdDate(LocalDate.now())
                .accommodation(accommodationRepository.findById(reviewDto.getAccommodationId())
                        .orElseThrow(() -> new NotFoundException("Not Found Accommodation")))
                .rating(reviewDto.getRating())
                .member(memberRepository.findById(reviewDto.getMemberId())
                        .orElseThrow(() -> new NotFoundException("Not Found Member")))
                .build();

        reviewRepository.save(review);
    }

    public void updateReview(ReviewDto reviewDto) throws NotFoundException {
        Review originReview = reviewRepository
                .findReviewByAccommodation_IdAndMember_Id(reviewDto.getAccommodationId(), reviewDto.getMemberId())
                .orElseThrow(() -> new NotFoundException("Not Found Review"));

        originReview.setContent(reviewDto.getContent());
        originReview.setRating(reviewDto.getRating());
        originReview.setCreatedDate(LocalDate.now());

        reviewRepository.save(originReview);
    }
}
