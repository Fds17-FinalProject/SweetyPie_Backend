package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.exception.DataNotFoundException;
import com.mip.sharebnb.exception.DuplicateValueExeption;
import com.mip.sharebnb.exception.InvalidInputException;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReservationRepository reservationRepository;

    private final MemberRepository memberRepository;

    public Review findReviewByReservationId(long reservationId) {

        return reviewRepository.findReviewByReservationId(reservationId)
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));
    }

    public void writeReview(ReviewDto reviewDto) {

        Reservation reservation = reservationRepository.findById(reviewDto.getReservationId())
                .orElseThrow(() -> new DataNotFoundException("Reservation Not Found"));

        if (reviewRepository.findReviewByReservationId(reviewDto.getReservationId()).isPresent()
                || reservation.getIsWrittenReview()) {
            throw new DuplicateValueExeption("Already Have a Review");
        }

        Accommodation accommodation = reservation.getAccommodation();
        if (accommodation == null) {
            throw new DataNotFoundException("Accommodation Not Found");
        }

        if (accommodation.getId() != reviewDto.getAccommodationId()) {
            throw new InvalidInputException("Accommodation Not Matched");
        }
      
        Member member = memberRepository.findById(reviewDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException("Member Not Found"));

        int newReviewNum = accommodation.getReviewNum() + 1;
        float newRating = (accommodation.getRating() * accommodation.getReviewNum() + reviewDto.getRating())
                / newReviewNum;

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

        Reservation reservation = reservationRepository.findById(reviewDto.getReservationId())
                .orElseThrow(() -> new DataNotFoundException("Reservation Not Found"));

        Accommodation accommodation = reservation.getAccommodation();
        if (accommodation == null) {
            throw new DataNotFoundException("Accommodation Not Found");
        }

        if (accommodation.getId() != reviewDto.getAccommodationId()) {
            throw new InvalidInputException("Accommodation Not Matched");
        }

        memberRepository.findById(reviewDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException("Member Not Found"));

        float originRating = accommodation.getRating();
        float newRating = originRating + (reviewDto.getRating() - originReview.getRating()) / accommodation.getReviewNum();
        accommodation.setRating(newRating);

        originReview.setAccommodation(accommodation);
        originReview.setContent(reviewDto.getContent());
        originReview.setRating(reviewDto.getRating());
        originReview.setCreatedDate(LocalDate.now());
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review Not Found"));

        Reservation reservation = review.getReservation();
        if (reservation == null) {
            throw new DataNotFoundException("Reservation Not Found");
        }
        reservation.setIsWrittenReview(false);


        Accommodation accommodation = reservation.getAccommodation();
        if (accommodation == null) {
            throw new DataNotFoundException("Accommodation Not Found");
        }

        accommodation.setRating((accommodation.getRating() * accommodation.getReviewNum() - review.getRating())
                / (accommodation.getReviewNum() - 1));
        accommodation.setReviewNum(accommodation.getReviewNum() - 1);
        accommodation.setHostReviewNum(accommodation.getHostReviewNum() - 1);

        reviewRepository.deleteById(id);
    }
}