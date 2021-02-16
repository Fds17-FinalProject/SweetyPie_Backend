package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.ReviewDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.model.Review;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.ReservationRepository;
import com.mip.sharebnb.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void findReviewByAccommodation_IdAndMember_Id() {
        when(reviewRepository.findReviewByReservationId(1)).thenReturn(mockReview());

        Review review = reviewService.findReviewByReservationId(1);

        assertThat(review.getContent()).isEqualTo("좋아요");
        assertThat(review.getRating()).isEqualTo(3);
    }

    @DisplayName("리뷰 등록")
    @Test
    void postReview() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation());
        when(memberRepository.findById(1L)).thenReturn(mockMember());

        reviewService.writeReview(mockReviewDto());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @DisplayName("리뷰 수정")
    @Test
    void updateReview() {
        when(reviewRepository.findReviewByReservationId(0)).thenReturn(mockReview());
        when(reservationRepository.findById(0L)).thenReturn(mockReservation());
        when(memberRepository.findById(1L)).thenReturn(mockMember());

        reviewService.updateReview(mockReviewDto());

        Review review = reviewService.findReviewByReservationId(0);

        assertThat(review.getContent()).isEqualTo("변경");
        assertThat(review.getRating()).isEqualTo(4);
    }

    @DisplayName("리뷰 삭제")
    @Test
    void deleteReview() {
        when(reviewRepository.findById(1L)).thenReturn(mockReview());

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    private Optional<Review> mockReview() {

        return Optional.of(Review.builder()
                .rating(3)
                .createdDate(LocalDate.now())
                .accommodation(null)
                .member(null)
                .content("좋아요")
                .reservation(mockReservation().get())
                .build());
    }

    private ReviewDto mockReviewDto() {

        return ReviewDto.builder()
                .content("변경")
                .rating(4)
                .accommodationId(mockAccommodation().get().getId())
                .memberId(mockMember().get().getId())
                .build();
    }

    private Optional<Accommodation> mockAccommodation() {
        return Optional.of(new Accommodation(1L, 0, "서울특별시", "마포구", "서울특별시 마포구 독막로 266", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", 4.56f, 125, "전체", "원룸", "이재복", 543, null, new ArrayList<>(), null, null));
    }

    private Optional<Member> mockMember() {
        Member member = new Member();

        member.setId(1L);
        member.setEmail("ddd@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        return Optional.of(member);
    }

    private Optional<Reservation> mockReservation() {
        Reservation reservation = new Reservation();

        LocalDate checkInDate = LocalDate.of(2020, 2, 22);
        LocalDate checkoutDate = LocalDate.of(2020, 2, 24);
        reservation.setId(2L);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setTotalPrice(50000);
        reservation.setTotalGuestNum(5);
        reservation.setIsWrittenReview(false);
        reservation.setAccommodation(mockAccommodation().get());

        return Optional.of(reservation);
    }
}