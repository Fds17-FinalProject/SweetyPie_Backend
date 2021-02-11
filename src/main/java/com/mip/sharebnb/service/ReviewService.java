package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.exception.DataNotFoundException;
import com.mip.sharebnb.exception.DuplicateValueExeption;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final AccommodationRepository accommodationRepository;

    private final MemberRepository memberRepository;

    private final ReservationRepository reservationRepository;

    public Review findReviewByAccommodationIdAndMemberId(long accommodationId, long memberId) {

        return reviewRepository.findReviewByAccommodation_IdAndMember_Id(accommodationId, memberId)
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));
    }

    public void writeReview(ReviewDto reviewDto) {

        if (reviewRepository.findReviewByAccommodation_IdAndMember_Id(reviewDto.getAccommodationId(), reviewDto.getMemberId()).isPresent()) {
            throw new DuplicateValueExeption("이미 작성한 리뷰가 있습니다.");
        }

        Reservation reservation = reservationRepository.findById(reviewDto.getReservationId())
                .orElseThrow(() -> new DataNotFoundException("Reservation Not Found"));

        Accommodation accommodation = accommodationRepository.findById(reviewDto.getAccommodationId())
                .orElseThrow(() -> new DataNotFoundException("Accommodation Not Found"));

        Member member = memberRepository.findById(reviewDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException("Member Not Found"));

        reservation.setIsWrittenReview(true);

        Review review = new Review();
        review.setRating(reviewDto.getRating());
        review.setContent(reviewDto.getContent());
        review.setAccommodation(accommodation);
        review.setMember(member);
        review.setReservation(reservation);

        reviewRepository.save(review);
    }

    public void updateReview(ReviewDto reviewDto) {
        Review originReview = reviewRepository
                .findReviewByAccommodation_IdAndMember_Id(reviewDto.getAccommodationId(), reviewDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));

        originReview.setContent(reviewDto.getContent());
        originReview.setRating(reviewDto.getRating());
        originReview.setCreatedDate(LocalDate.now());

        reviewRepository.save(originReview);
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));

        reviewRepository.deleteById(id);
    }
}
