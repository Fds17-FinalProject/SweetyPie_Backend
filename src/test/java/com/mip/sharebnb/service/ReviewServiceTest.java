package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("작성한 리뷰 가져 오기")
    @Test
    void findReviewByAccommodation_IdAndMember_Id() {
        when(reviewRepository.findReviewByAccommodation_IdAndMember_Id(1, 1)).thenReturn(mockReview());

        Review review = reviewService.findReviewByAccommodation_IdAndMember_Id(1, 1).get();

        assertThat(review.getContent()).isEqualTo("좋아요");
        assertThat(review.getRating()).isEqualTo(3);
    }

    @Test
    void writeReview() {
    }

    private Optional<Review> mockReview() {

        return Optional.of(Review.builder()
                .rating(3)
                .createdDate(LocalDate.now())
                .accommodation(null)
                .member(null)
                .content("좋아요")
                .build());
    }
}