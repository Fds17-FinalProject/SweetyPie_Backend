package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.model.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.config.location="
        + "classpath:test.yml")
class ReviewRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("작성한 리뷰 가져오기")
    @Test
    void findReviewByReservationId() {
        Accommodation accommodation = givenAccommodation();
        Member member = givenMember();
        Reservation reservation = givenReservation(accommodation, member);
        givenReview(reservation);

        List<Review> reviews = reviewRepository.findAll();

        Review findReview = reviewRepository.findReviewByReservationId(reviews.get(reviews.size() - 1).getReservation().getId()).get();

        assertThat(findReview.getRating()).isEqualTo(3);
    }

    private Accommodation givenAccommodation() {

        return accommodationRepository.save(new Accommodation(null, 0, "대구광역시", "수성구", "대구광역시 수성구 xx로", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포역 1번 출구 앞", "버스 7016", "깨끗해요", "착해요", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, new ArrayList<>(), null));
    }

    private Member givenMember() {
        Member member = new Member();
        member.setEmail("d64u90@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        return memberRepository.save(member);
    }

    private Reservation givenReservation(Accommodation accommodation, Member member) {
        Reservation reservation = new Reservation();

        LocalDate checkIn = LocalDate.of(2022, 3, 4);
        LocalDate checkout = LocalDate.of(2022, 3, 9);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckoutDate(checkout);
        reservation.setTotalGuestNum(3);
        reservation.setTotalPrice(20000);
        reservation.setPaymentDate(LocalDate.now());
        reservation.setAccommodation(accommodation);
        reservation.setMember(member);

        for (LocalDate localDate = reservation.getCheckInDate(); localDate.isBefore(reservation.getCheckoutDate()); localDate = localDate.plusDays(1)) {
            BookedDate bookedDate = new BookedDate();
            bookedDate.setDate(localDate);
            bookedDate.setReservation(reservation);
        }

        return reservationRepository.save(reservation);
    }

    private Review givenReview(Reservation reservation) {
        Review review = new Review(null, 3f, "테스트 리뷰", LocalDate.now(), reservation.getMember(), reservation.getAccommodation(), reservation);

        return reviewRepository.save(review);
    }
}