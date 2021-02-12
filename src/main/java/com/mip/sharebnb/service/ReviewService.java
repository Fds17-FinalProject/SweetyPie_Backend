package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.exception.DataNotFoundException;
import com.mip.sharebnb.exception.DuplicateValueExeption;
import com.mip.sharebnb.exception.InvalidInputException;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.ReservationRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReservationRepository reservationRepository;

    public Review findReviewByReservationId(long reservationId) {

        return reviewRepository.findReviewByReservationId(reservationId)
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));
    }

    public void writeReview(ReviewDto reviewDto) {

        if (reviewRepository.findReviewByReservationId(reviewDto.getReservationId()).isPresent()) {
            throw new DuplicateValueExeption("Already Have a Review");
        }

        Reservation reservation = reservationRepository.findById(reviewDto.getReservationId())
                .orElseThrow(() -> new DataNotFoundException("Reservation Not Found"));

        Accommodation accommodation = reservation.getAccommodation();
        if (accommodation == null) {
            throw new DataNotFoundException("Accommodation Not Found");
        }

        if (accommodation.getId() != reviewDto.getAccommodationId()) {
            throw new InvalidInputException("Accommodation Not Matched");
        }

        Member member = reservation.getMember();
        if (member == null) {
            throw new DataNotFoundException("Member Not Found");
        }

        if (member.getId() != reviewDto.getMemberId()) {
            throw new InvalidInputException("Member Not Matched");
        }

        int newReviewNum = accommodation.getReviewNum() + 1;
        float newRating = (accommodation.getRating() * accommodation.getReviewNum() + reviewDto.getRating())
                / newReviewNum;

        reservation.setIsWrittenReview(true);
        accommodation.setRating(newRating);
        accommodation.setReviewNum(newReviewNum);
        accommodation.setHostReviewNum(accommodation.getHostReviewNum() + 1);

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
                .findReviewByReservationId(reviewDto.getReservationId())
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));

        originReview.setContent(reviewDto.getContent());
        originReview.setRating(reviewDto.getRating());
        originReview.setCreatedDate(LocalDate.now());

        reviewRepository.save(originReview);
    }

    public void deleteReview(Long id) {
        Review originReview = reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));

        originReview.getReservation().setIsWrittenReview(false);

        reviewRepository.deleteById(id);
    }
}