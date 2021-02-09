package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.ReviewRepository;
import javassist.NotFoundException;
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

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void findReviewByAccommodation_IdAndMember_Id() {
        when(reviewRepository.findReviewByAccommodation_IdAndMember_Id(1L, 1L)).thenReturn(mockReview());

        Review review = reviewService.findReviewByAccommodation_IdAndMember_Id(1L, 1L).get();

        assertThat(review.getContent()).isEqualTo("좋아요");
        assertThat(review.getRating()).isEqualTo(3);
    }

    @DisplayName("리뷰 수정")
    @Test
    void updateReview() throws NotFoundException {
        when(reviewRepository.findReviewByAccommodation_IdAndMember_Id(1L, 1L)).thenReturn(mockReview());

        reviewService.updateReview(mockReviewDto());

        Review review = reviewService.findReviewByAccommodation_IdAndMember_Id(1L, 1L).get();

        assertThat(review.getContent()).isEqualTo("변경");
        assertThat(review.getRating()).isEqualTo(4);
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

    private ReviewDto mockReviewDto() {

        return ReviewDto.builder()
                .content("변경")
                .rating(4)
                .accommodationId(1)
                .memberId(1)
                .build();
    }
}