package com.sweetypie.sweetypie.service;

import com.sweetypie.sweetypie.dto.ReviewDto;
import com.sweetypie.sweetypie.exception.DataNotFoundException;
import com.sweetypie.sweetypie.exception.DuplicateValueExeption;
import com.sweetypie.sweetypie.exception.InputNotValidException;
import com.sweetypie.sweetypie.model.Accommodation;
import com.sweetypie.sweetypie.model.Member;
import com.sweetypie.sweetypie.model.Reservation;
import com.sweetypie.sweetypie.model.Review;
import com.sweetypie.sweetypie.repository.MemberRepository;
import com.sweetypie.sweetypie.repository.ReservationRepository;
import com.sweetypie.sweetypie.repository.ReviewRepository;
import com.sweetypie.sweetypie.security.jwt.TokenProvider;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private TokenProvider provider;

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void findReviewByAccommodation_IdAndMember_Id() {
        when(reviewRepository.findReviewByReservationId(1)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));
        when(memberRepository.findById(0L)).thenReturn(mockMember());

        Review review = reviewService.findReviewByReservationId("token", 1);

        assertThat(review.getContent()).isEqualTo("좋아요");
        assertThat(review.getRating()).isEqualTo(3);
    }

    @DisplayName("작성한 리뷰 가져오기 (리뷰 없음)")
    @Test
    void findReviewException1() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.findReviewByReservationId("token", 1));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Review Not Found");
    }

    @DisplayName("작성한 리뷰 가져오기 (회원 없음)")
    @Test
    void findReviewException2() {
        when(reviewRepository.findReviewByReservationId(1L)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.findReviewByReservationId("token", 1));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Member Not Found");
    }

    @DisplayName("작성한 리뷰 가져오기 (작성자 불일치)")
    @Test
    void findReviewException3() {
        when(reviewRepository.findReviewByReservationId(1L)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));
        when(memberRepository.findById(0L)).thenReturn(Optional.of(new Member()));

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class,
                () -> reviewService.findReviewByReservationId("token", 1));

        assertThat(inputNotValidException.getMessage()).isEqualTo("토큰의 회원 정보와 리뷰 작성자가 일치하지 않습니다.");
    }

    @DisplayName("리뷰 등록")
    @Test
    void postReview() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()));
        when(memberRepository.findById(0L)).thenReturn(mockMember());

        reviewService.writeReview("token", mockReviewDto());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @DisplayName("리뷰 등록 (없는 예약)")
    @Test
    void postReviewException1() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.writeReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Reservation Not Found");
    }

    @DisplayName("리뷰 등록 (중복)")
    @Test
    void postReviewException2() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()));
        when(reviewRepository.findReviewByReservationId(0L)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));

        DuplicateValueExeption duplicateValueExeption = assertThrows(DuplicateValueExeption.class,
                () -> reviewService.writeReview("token", mockReviewDto()));

        assertThat(duplicateValueExeption.getMessage()).isEqualTo("Already Have a Review");
    }

    @DisplayName("리뷰 등록 (체크인 이전)")
    @Test
    void postReviewException3() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 3, 1), mockAccommodation().get()));

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class,
                () -> reviewService.writeReview("token", mockReviewDto()));

        assertThat(inputNotValidException.getMessage()).isEqualTo("숙소 이용 전에 리뷰를 작성할 수 없습니다.");
    }

    @DisplayName("리뷰 등록 (없는 숙소)")
    @Test
    void postReviewException4() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 1), null));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.writeReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Accommodation Not Found");
    }

    @DisplayName("리뷰 등록 (없는 회원)")
    @Test
    void postReviewException5() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.writeReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Member Not Found");
    }

    @DisplayName("리뷰 등록 (다른 회원)")
    @Test
    void postReviewException6() {
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()));

        when(memberRepository.findById(0L)).thenReturn(Optional.of(new Member()));

        InputNotValidException inputNotValidException = assertThrows(InputNotValidException.class,
                () -> reviewService.writeReview("token", mockReviewDto()));

        assertThat(inputNotValidException.getMessage()).isEqualTo("토큰의 회원 정보와 예약 회원이 일치하지 않습니다.");
    }

    @DisplayName("리뷰 수정")
    @Test
    void updateReview() {
        when(reviewRepository.findReviewByReservationId(0)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()));
        when(memberRepository.findById(0L)).thenReturn(mockMember());

        reviewService.updateReview("token", mockReviewDto());

        Review review = reviewService.findReviewByReservationId("token", 0);

        assertThat(review.getContent()).isEqualTo("변경");
        assertThat(review.getRating()).isEqualTo(4);
    }

    @DisplayName("리뷰 수정 (없는 리뷰)")
    @Test
    void updateReviewException1() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.updateReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Review Not Found");
    }

    @DisplayName("리뷰 수정 (없는 예약)")
    @Test
    void updateReviewException2() {
        when(reviewRepository.findReviewByReservationId(0)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.updateReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Reservation Not Found");
    }

    @DisplayName("리뷰 수정 (없는 숙소)")
    @Test
    void updateReviewException3() {
        when(reviewRepository.findReviewByReservationId(0)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 1), null));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.updateReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Accommodation Not Found");
    }

    @DisplayName("리뷰 수정 (없는 회원)")
    @Test
    void updateReviewException4() {
        when(reviewRepository.findReviewByReservationId(0)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));
        when(reservationRepository.findById(0L)).thenReturn(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.updateReview("token", mockReviewDto()));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Member Not Found");
    }

    @DisplayName("리뷰 삭제")
    @Test
    void deleteReview() {
        when(reviewRepository.findById(1L)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));
        when(memberRepository.findById(0L)).thenReturn(mockMember());

        reviewService.deleteReview("token", 1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @DisplayName("리뷰 삭제 (없는 리뷰)")
    @Test
    void deleteReviewException1() {
        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.deleteReview("token", 0L));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Review Not Found");
    }

    @DisplayName("리뷰 삭제 (없는 예약)")
    @Test
    void deleteReviewException2() {
        when(reviewRepository.findById(0L)).thenReturn(Optional.of(mockReview(null).get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.deleteReview("token", 0L));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Reservation Not Found");
    }

    @DisplayName("리뷰 삭제 (없는 숙소)")
    @Test
    void deleteReviewException3() {
        when(reviewRepository.findById(0L)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), null).get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.deleteReview("token", 0L));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Accommodation Not Found");
    }

    @DisplayName("리뷰 삭제 (없는 회원)")
    @Test
    void deleteReviewException4() {
        when(reviewRepository.findById(0L)).thenReturn(mockReview(mockReservation(LocalDate.of(2021, 2, 22), mockAccommodation().get()).get()));

        DataNotFoundException dataNotFoundException = assertThrows(DataNotFoundException.class,
                () -> reviewService.deleteReview("token", 0L));

        assertThat(dataNotFoundException.getMessage()).isEqualTo("Member Not Found");
    }

    private Optional<Review> mockReview(Reservation reservation) {

        return Optional.of(Review.builder()
                .rating(3)
                .createdDate(LocalDate.now())
                .accommodation(null)
                .member(mockMember().get())
                .content("좋아요")
                .reservation(reservation)
                .build());
    }

    private ReviewDto mockReviewDto() {

        return ReviewDto.builder()
                .content("변경")
                .rating(4)
                .accommodationId(mockAccommodation().get().getId())
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

    private Optional<Reservation> mockReservation(LocalDate checkInDate, Accommodation accommodation) {
        Reservation reservation = new Reservation();

        LocalDate checkoutDate = LocalDate.of(2021, 3, 10);
        reservation.setId(2L);
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setTotalPrice(50000);
        reservation.setTotalGuestNum(5);
        reservation.setWrittenReview(false);
        reservation.setMember(mockMember().get());
        reservation.setAccommodation(accommodation);

        return Optional.of(reservation);
    }
}