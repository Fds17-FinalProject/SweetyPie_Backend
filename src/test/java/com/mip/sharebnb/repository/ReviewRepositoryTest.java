package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.Accommodation;
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
        + "classpath:application.yml,"
        + "classpath:datasource.yml")
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
        givenReview();

        List<Review> reviews = reviewRepository.findAll();

        Review review = reviewRepository.findReviewByReservationId(reviews.get(reviews.size() - 1).getReservation().getId()).get();

        assertThat(review.getRating()).isEqualTo(3);
    }

    private void givenReview() {
        Member member = givenMember();
        Accommodation accommodation = givenAccommodation();

        Review review = new Review(null, 3, "좋아요", LocalDate.now(), member, accommodation, givenReservation(member, accommodation));

        reviewRepository.save(review);
    }

    private Member givenMember() {
        Member member = new Member();
        member.setEmail("ddd@gmail.com");
        member.setName("이재복");
        member.setPassword("1234");
        member.setContact("12378");
        member.setBirthDate(LocalDate.of(1993, 5, 1));

        return memberRepository.save(member);
    }

    private Accommodation givenAccommodation() {
        Accommodation accommodation = new Accommodation(null, "서울특별시", "마포구", "서울특별시 마포구", "원룸", 1, 1, 1, 40000, 2, "010-1234-5678", 36.141f, 126.531f, "마포", "버스 7016", "깔끔", "", 4.56f, 125, "전체", "원룸", "이재복", 543, null, null, null, null, null);

        return accommodationRepository.save(accommodation);
    }

    private Reservation givenReservation(Member member, Accommodation accommodation) {
        Reservation reservation = new Reservation(null, LocalDate.of(2022, 3, 5), LocalDate.of(2022, 3, 10)
                , 3, 80000, false, null, null, null, member, accommodation, new ArrayList<>());

        return reservationRepository.save(reservation);
    }
}